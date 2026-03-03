package model

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.api.Actor

// basic user data model/entity
data class User(
    val id: Id,
    val permissions: Set<Permission>
)

// user/session context object
data class UserContext(
    val user: User,
    val sessionData: String? = null // etc
) : Actor by Actor.create(AppModel, user.id, user.permissions)
