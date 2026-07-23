package dev.stashy.ktgrants.permissions.impl.tree

import dev.stashy.ktgrants.permissions.*
import dev.stashy.ktgrants.permissions.impl.processors.PermissionProcessor

internal class TreeResolver(
    private val processors: List<PermissionProcessor>,
    private val scopeLookup: Lookup<Scope>,
    private val subjectLookup: Lookup<Subject>,
    private val grantLookup: Lookup<Grant>,
) : PermissionPolicy {
    override fun process(permissions: Sequence<Permission>): PermissionCollection {
        val result = processors.fold(permissions) { acc, processor ->
            processor.process(acc)
        }
        return TreePermissionCollection(PermissionTree.build(result), scopeLookup, subjectLookup, grantLookup)
    }
}

internal class PermissionTree(
    val root: Map<Scope, Map<Subject, Set<Grant>>>
) {
    companion object {
        fun build(permissions: Sequence<Permission>): PermissionTree {
            val tree = mutableMapOf<Scope, MutableMap<Subject, MutableSet<Grant>>>()
            permissions.forEach { perm ->
                tree.getOrPut(perm.scope) { mutableMapOf() }
                    .getOrPut(perm.subject) { mutableSetOf() }
                    .add(perm.grant)
            }
            return PermissionTree(tree)
        }
    }
}

internal data class TreePermissionCollection(
    private val tree: PermissionTree,
    private val scopeLookup: Lookup<Scope>,
    private val subjectLookup: Lookup<Subject>,
    private val grantLookup: Lookup<Grant>
) : PermissionCollection {
    override fun includes(permission: Permission): Boolean {
        val scopes = scopeLookup.resolve(permission.scope)
        val subjects = subjectLookup.resolve(permission.subject)
        val grants = grantLookup.resolve(permission.grant)

        for (s in scopes) {
            val subjectsMap = tree.root[s] ?: continue
            for (sub in subjects) {
                val grantsSet = subjectsMap[sub] ?: continue
                for (g in grants) {
                    if (g in grantsSet) return true
                }
            }
        }
        return false
    }
}
