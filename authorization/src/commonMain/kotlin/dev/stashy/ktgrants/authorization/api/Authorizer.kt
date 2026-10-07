package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.authorization.Action
import dev.stashy.ktgrants.authorization.ContextualRequirementBuilder
import dev.stashy.ktgrants.authorization.RequirementBuilder
import dev.stashy.ktgrants.authorization.RequirementContextImpl
import dev.stashy.ktgrants.permissions.PermissionConfig

public interface ContextualAuthorizer<E : Any, P : PermissionConfig, C> {
    context(config: P)
    public fun verify(entity: E, actor: Actor, action: Action, context: C): Boolean

    public companion object {
        public operator fun <E : Any, P : PermissionConfig, C> invoke(
            builder: ContextualRequirementBuilder<E, P, C>
        ): ContextualAuthorizer<E, P, C> = ContextualAuthorizerDelegate(builder)
    }
}

public interface Authorizer<E : Any, P : PermissionConfig> : ContextualAuthorizer<E, P, Unit> {
    context(config: P)
    public fun verify(entity: E, actor: Actor, action: Action): Boolean

    public companion object {
        public operator fun <E : Any, P : PermissionConfig> invoke(
            builder: RequirementBuilder<E, P>
        ): Authorizer<E, P> = AuthorizerAdapter(builder)
    }
}

internal class AuthorizerAdapter<E : Any, P : PermissionConfig>(
    builder: RequirementBuilder<E, P>
) : Authorizer<E, P>,
    ContextualAuthorizer<E, P, Unit> by ContextualAuthorizerDelegate(builder) {
    context(config: P)
    override fun verify(entity: E, actor: Actor, action: Action): Boolean =
        verify(entity, actor, action, Unit)
}

internal class ContextualAuthorizerDelegate<E : Any, P : PermissionConfig, C>(
    private val builder: ContextualRequirementBuilder<E, P, C>
) : ContextualAuthorizer<E, P, C> {
    context(config: P)
    override fun verify(entity: E, actor: Actor, action: Action, context: C): Boolean {
        val requirementContext = RequirementContextImpl(action, entity, actor)
        builder.invoke(requirementContext, config, context)
        return requirementContext.result()
    }
}
