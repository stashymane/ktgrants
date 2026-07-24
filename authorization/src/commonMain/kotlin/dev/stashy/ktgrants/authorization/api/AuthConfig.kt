package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.authorization.dsl.AuthDsl
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionConfig
import dev.stashy.ktgrants.permissions.SubjectProvider

public interface AuthConfig : PermissionConfig, AuthDsl {
    public fun actor(subject: SubjectProvider, permissions: Set<Permission>): Actor =
        Actor.create(policy, subject, permissions)
}
