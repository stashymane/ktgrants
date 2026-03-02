package model

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection

data class User(
    val id: Id,
    val permissions: Set<Permission>
) : PermissionCollection by AppModel.process(permissions)
