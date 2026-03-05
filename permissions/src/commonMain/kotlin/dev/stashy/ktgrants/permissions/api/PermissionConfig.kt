package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.SubjectProvider

public interface PermissionConfig : GrantModel {
    public val resolver: PermissionResolver

    public fun actor(subject: SubjectProvider, permissions: Set<Permission>): Actor =
        Actor.create(resolver, subject, permissions)
}
