package dev.stashy.ktgrants.permissions.data.model

import dev.stashy.ktgrants.permissions.data.Permission
import dev.stashy.ktgrants.permissions.data.PermissionContainer
import dev.stashy.ktgrants.permissions.data.PermissionSet

internal class ExplicitModel() : PermissionModel {
    override fun process(permissions: Sequence<Permission>): PermissionContainer =
        PermissionSet(permissions)
}
