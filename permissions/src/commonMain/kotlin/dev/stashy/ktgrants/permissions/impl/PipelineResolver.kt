package dev.stashy.ktgrants.permissions.impl

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection
import dev.stashy.ktgrants.permissions.PermissionPolicy
import dev.stashy.ktgrants.permissions.PermissionSet
import dev.stashy.ktgrants.permissions.impl.resolvers.Resolver

internal class PipelineResolver(
    val generators: List<Resolver.Generator>,
    val checkers: List<Resolver.Checker>
) : PermissionPolicy {
    override fun process(permissions: Sequence<Permission>): PermissionCollection {
        val permissionSequence = generators.fold(permissions) { acc, generator ->
            generator.process(acc)
        }

        val permissionSet = PermissionSet(permissionSequence)

        val checkedSet = PermissionCollection { permission ->
            val sequence = checkers.fold(sequenceOf(permission)) { sequence, checker ->
                checker.expand(sequence)
            }
            permissionSet.includesAny(sequence)
        }
        return checkedSet
    }
}
