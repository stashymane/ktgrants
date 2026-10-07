package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.authorization.Action
import dev.stashy.ktgrants.authorization.Authorization
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionConfig
import dev.stashy.ktgrants.permissions.PermissionPolicy
import dev.stashy.ktgrants.permissions.SubjectProvider
import dev.stashy.ktgrants.permissions.api.PermissionHolder

public interface Actor : SubjectProvider, PermissionHolder {
    context(config: P)
    public fun <E, P> authorize(
        action: Action,
        entity: E,
    ): Boolean
            where E : Authorized<E, P>, P : PermissionConfig =
        entity.authorizer.verify(entity, this, action, Unit)

    context(config: P)
    public fun <E, P, C> authorize(
        action: Action,
        entity: E,
        context: C,
    ): Boolean
            where E : ContextuallyAuthorized<E, P, C>, P : PermissionConfig =
        entity.authorizer.verify(entity, this, action, context)

    public companion object {
        public fun create(
            policy: PermissionPolicy,
            subject: SubjectProvider,
            permissions: Set<Permission>
        ): Actor = ActorDelegate(policy, subject, permissions)
    }
}

context(config: P)
public inline fun <E, P> Actor.authorize(
    builder: P.() -> Authorization<E>,
): Boolean
        where E : Authorized<E, P>, P : AuthConfig {
    val request = builder(config)
    return authorize(request.action, request.entity)
}

internal class ActorDelegate(
    policy: PermissionPolicy,
    subject: SubjectProvider,
    permissions: Set<Permission>
) : Actor,
    PermissionHolder by PermissionHolder.create(policy, permissions),
    SubjectProvider by subject
