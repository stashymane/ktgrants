package dev.stashy.ktgrants.permissions.model

import dev.stashy.ktgrants.annotations.KtgrantDsl
import dev.stashy.ktgrants.permissions.*

internal class GraphModel(
    private val grantMap: Map<Grant, Set<Grant>> = mapOf()
) : PermissionModel {
    override fun process(permissions: Sequence<Permission>): PermissionCollection = PermissionSet(
        permissions.fold(sequenceOf()) { sequence, permission ->
            val grants = grantMap[permission.grant]
                ?: return@fold sequence + permission
            sequence + grants.asSequence().map { permission.copy(grant = it) }
        }
    )
}

@KtgrantDsl
public class GraphConfig internal constructor() {
    private val grantGraph = mutableMapOf<Grant, Set<Grant>>()

    public infix fun Grant.provides(grants: Set<Grant>): Unit = assign(this, grants)
    public infix fun Grant.provides(grant: Grant): Unit = assign(this, setOf(grant))

    public fun assign(key: Grant, values: Set<Grant>) {
        require(!grantGraph.containsKey(key)) { "Grant \"$key\" has already been defined" }
        grantGraph[key] = values
    }

    internal fun build(): PermissionModel = GraphModel(flatten(grantGraph))
}

private fun flatten(graph: Map<Grant, Set<Grant>>): Map<Grant, Set<Grant>> {
    val result = mutableMapOf<Grant, Set<Grant>>()
    val visited = mutableSetOf<Grant>()

    fun resolve(grant: Grant): Set<Grant> {
        if (grant in result) return result[grant]!!
        if (grant in visited) return emptySet() // circular dependency

        visited.add(grant)

        val directGrants = graph[grant] ?: return setOf(grant)
        val resolvedGrants = directGrants.flatMapTo(mutableSetOf(), ::resolve)

        resolvedGrants.add(grant)

        result[grant] = resolvedGrants
        visited.remove(grant)
        return resolvedGrants
    }

    graph.keys.forEach(::resolve)
    return result
}
