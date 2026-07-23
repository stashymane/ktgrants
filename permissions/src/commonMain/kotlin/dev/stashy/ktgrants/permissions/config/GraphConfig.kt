package dev.stashy.ktgrants.permissions.config

import dev.stashy.ktgrants.annotations.KtgrantDsl
import dev.stashy.ktgrants.permissions.*

@KtgrantDsl
public sealed interface GraphConfig<T> {
    /**
     * Assigns the [key] to additionally include [values]
     */
    public fun assign(key: T, values: Set<T>)
}

context(config: GraphConfig<T>)
public infix fun <T> T.provides(values: Set<T>): Unit = config.assign(this, values)

context(config: GraphConfig<Scope>)
public infix fun <T : ScopeProvider> T.provides(values: Set<T>): Unit = config.assign(
    this.toScope(), values.map(ScopeProvider::toScope).toSet()
)

context(config: GraphConfig<Subject>)
public infix fun <T : SubjectProvider> T.provides(values: Set<T>): Unit = config.assign(
    this.toSubject(), values.map(SubjectProvider::toSubject).toSet()
)

context(config: GraphConfig<Grant>)
public infix fun <T : GrantProvider> T.provides(values: Set<T>): Unit = config.assign(
    this.toGrant(), values.map(GrantProvider::toGrant).toSet()
)

internal open class GraphConfigImpl<T> : GraphConfig<T> {
    private val graph = mutableMapOf<T, Set<T>>()

    override fun assign(key: T, values: Set<T>) {
        require(!graph.containsKey(key)) { "Value \"$key\" has already been defined" }
        graph[key] = values
    }

    internal fun buildInverted(): Map<T, Set<T>> {
        val flattened = flatten(graph)
        val result = mutableMapOf<T, MutableSet<T>>()
        for ((provider, providedSet) in flattened) {
            for (provided in providedSet) {
                if (provider == provided) continue
                result.getOrPut(provided) { mutableSetOf() }.add(provider)
            }
        }
        return result
    }
}

private fun <T> flatten(graph: Map<T, Set<T>>): Map<T, Set<T>> {
    val result = mutableMapOf<T, Set<T>>()
    val visited = mutableSetOf<T>()

    fun resolve(value: T): Set<T> {
        if (value in result) return result[value]!!
        if (value in visited) return emptySet() // circular dependency

        visited.add(value)

        val directValues = graph[value] ?: return setOf(value)
        val resolvedValues = directValues.flatMapTo(mutableSetOf(), ::resolve)

        resolvedValues.add(value)

        result[value] = resolvedValues
        visited.remove(value)
        return resolvedValues
    }

    graph.keys.forEach(::resolve)
    return result
}
