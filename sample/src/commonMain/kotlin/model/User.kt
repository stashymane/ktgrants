package model

import dev.stashy.ktgrants.authorization.api.Actor
import dev.stashy.ktgrants.permissions.Permission

// basic user data model/entity
data class User(
    val id: Id,
    val permissions: Set<Permission>
)

// user/session context object
data class UserContext(
    val user: User,
    val sessionData: String? = null // etc
) : Actor by PermissionModel.actor(user.id, user.permissions)
