package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.authorization.dsl.AuthorizeDsl
import dev.stashy.ktgrants.authorization.dsl.AuthorizerDsl
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionConfig
import dev.stashy.ktgrants.permissions.SubjectProvider

public interface AuthConfig : PermissionConfig, AuthorizerDsl, AuthorizeDsl {
    public fun actor(subject: SubjectProvider, permissions: Set<Permission>): Actor =
        Actor.create(policy, subject, permissions)
}
