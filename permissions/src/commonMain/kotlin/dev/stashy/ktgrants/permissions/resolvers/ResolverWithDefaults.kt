package dev.stashy.ktgrants.permissions.resolvers

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection
import dev.stashy.ktgrants.permissions.PermissionResolver

internal class ResolverWithDefaults(
    private val defaults: Set<Permission>,
    private val wrapped: PermissionResolver
) : PermissionResolver by wrapped {
    override fun process(permissions: Sequence<Permission>): PermissionCollection =
        wrapped.process(defaults.asSequence() + permissions)
}
