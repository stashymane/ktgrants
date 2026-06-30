package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.SubjectProvider
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl

public interface PermissionConfig : GrantModel, GrantDsl {
    public val resolver: PermissionResolver

    public fun actor(subject: SubjectProvider, permissions: Set<Permission>): Actor =
        Actor.create(resolver, subject, permissions)
}
