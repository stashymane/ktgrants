package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionModel
import dev.stashy.ktgrants.permissions.SubjectProvider

public interface Actor : SubjectProvider, PermissionOwner {
    public companion object {
        public fun create(model: PermissionModel, subject: SubjectProvider, permissions: Set<Permission>): Actor =
            ActorDelegate(model, subject, permissions)
    }
}

private class ActorDelegate(
    model: PermissionModel,
    subject: SubjectProvider,
    permissions: Set<Permission>
) : Actor,
    PermissionOwner by PermissionOwner.create(model, permissions),
    SubjectProvider by subject
