package model

import dev.stashy.ktgrants.authorization.api.Actor
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.SubjectProvider

// basic user data model/entity
data class User(
    val id: Id,
    val permissions: Set<Permission>
) : SubjectProvider by id

// user/session context object
data class UserContext(
    val user: User,
    val sessionData: String? = null,
) : Actor by PermissionModel.actor(user, user.permissions)
