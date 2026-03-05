package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection
import dev.stashy.ktgrants.permissions.PermissionResolver

public interface PermissionOwner {
    public val permissions: PermissionCollection

    public fun hasPermission(permission: Permission): Boolean = permissions.includes(permission)

    public companion object {
        public fun create(model: PermissionResolver, permissions: Set<Permission>): PermissionOwner =
            PermissionOwnerDelegate(model.process(permissions))
    }
}

private class PermissionOwnerDelegate(override val permissions: PermissionCollection) : PermissionOwner
