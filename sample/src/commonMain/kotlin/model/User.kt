package model

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionContainer

data class User(
    val id: Id,
    val permissions: Set<Permission>
) : PermissionContainer by AppModel.process(permissions)
