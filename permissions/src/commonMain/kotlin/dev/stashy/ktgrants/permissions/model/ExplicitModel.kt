package dev.stashy.ktgrants.permissions.model

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionContainer
import dev.stashy.ktgrants.permissions.PermissionModel
import dev.stashy.ktgrants.permissions.PermissionSet

internal class ExplicitModel() : PermissionModel {
    override fun process(permissions: Sequence<Permission>): PermissionContainer =
        PermissionSet(permissions)
}
