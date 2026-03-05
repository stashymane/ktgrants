package dev.stashy.ktgrants.permissions.resolvers

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection
import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.PermissionSet

internal class ExplicitResolver() : PermissionResolver {
    override fun process(permissions: Sequence<Permission>): PermissionCollection =
        PermissionSet(permissions)
}
