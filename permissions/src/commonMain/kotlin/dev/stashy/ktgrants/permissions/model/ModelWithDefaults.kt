package dev.stashy.ktgrants.permissions.model

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionContainer
import dev.stashy.ktgrants.permissions.PermissionModel

internal class ModelWithDefaults(
    private val defaults: Set<Permission>,
    private val wrapped: PermissionModel
) : PermissionModel by wrapped {
    override fun process(permissions: Sequence<Permission>): PermissionContainer =
        wrapped.process(defaults.asSequence() + permissions)
}
