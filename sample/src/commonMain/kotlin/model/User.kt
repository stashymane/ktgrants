package model

import dev.stashy.ktgrants.permissions.data.Permission
import dev.stashy.ktgrants.permissions.data.PermissionContainer

data class User(
    val id: Id,
    val permissions: Set<Permission>
) : PermissionContainer by AppModel.process(permissions)
