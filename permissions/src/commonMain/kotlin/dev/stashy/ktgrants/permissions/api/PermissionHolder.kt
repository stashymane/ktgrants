package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection
import dev.stashy.ktgrants.permissions.PermissionPolicy

public interface PermissionHolder {
    public val permissions: PermissionCollection

    public fun hasPermission(permission: Permission): Boolean = permissions.includes(permission)

    public companion object {
        public fun create(policy: PermissionPolicy, permissions: Set<Permission>): PermissionHolder =
            PermissionHolderDelegate(policy.process(permissions))
    }
}

private class PermissionHolderDelegate(override val permissions: PermissionCollection) : PermissionHolder

context(config: T)
public inline fun <T : PermissionConfig> PermissionHolder.hasPermission(builder: T.() -> Permission): Boolean =
    this.hasPermission(builder(config))
