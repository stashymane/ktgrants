package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.SubjectProvider

public interface Actor : SubjectProvider, PermissionOwner {
    public companion object {
        public fun create(resolver: PermissionResolver, subject: SubjectProvider, permissions: Set<Permission>): Actor =
            ActorDelegate(resolver, subject, permissions)
    }
}

internal class ActorDelegate(
    resolver: PermissionResolver,
    subject: SubjectProvider,
    permissions: Set<Permission>
) : Actor,
    PermissionOwner by PermissionOwner.create(resolver, permissions),
    SubjectProvider by subject
