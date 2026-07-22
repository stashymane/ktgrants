package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionPolicy
import dev.stashy.ktgrants.permissions.SubjectProvider

public interface Actor : SubjectProvider, PermissionHolder {
    public companion object {
        public fun create(policy: PermissionPolicy, subject: SubjectProvider, permissions: Set<Permission>): Actor =
            ActorDelegate(policy, subject, permissions)
    }
}

internal class ActorDelegate(
    policy: PermissionPolicy,
    subject: SubjectProvider,
    permissions: Set<Permission>
) : Actor,
    PermissionHolder by PermissionHolder.create(policy, permissions),
    SubjectProvider by subject
