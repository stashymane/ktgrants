package dev.stashy.ktgrants.permissions.data.model

import dev.stashy.ktgrants.permissions.data.Permission
import dev.stashy.ktgrants.permissions.data.PermissionContainer

internal class ModelWithDefaults(
    private val defaults: Set<Permission>,
    private val wrapped: PermissionModel
) : PermissionModel by wrapped {
    override fun process(permissions: Sequence<Permission>): PermissionContainer =
        wrapped.process(defaults.asSequence() + permissions)
}
