package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.authorization.Action
import dev.stashy.ktgrants.authorization.ContextualRequirementBuilder
import dev.stashy.ktgrants.authorization.RequirementBuilder
import dev.stashy.ktgrants.authorization.RequirementContextImpl
import dev.stashy.ktgrants.permissions.PermissionConfig

public interface Authorizer<E : Any, P : PermissionConfig> {
    context(config: P)
    public fun verify(entity: E, actor: Actor, action: Action): Boolean

    public companion object {
        public operator fun <E : Any, P : PermissionConfig> invoke(
            builder: RequirementBuilder<E, P>
        ): Authorizer<E, P> = AuthorizerDelegate(builder)
    }
}

public interface ContextualAuthorizer<E, P : PermissionConfig, C> {
    context(config: P)
    public fun verify(entity: E, actor: Actor, action: Action, context: C): Boolean

    public companion object {
        public operator fun <E, P : PermissionConfig, C> invoke(
            builder: ContextualRequirementBuilder<E, P, C>
        ): ContextualAuthorizer<E, P, C> =
            ContextualAuthorizerDelegate(builder)
    }
}

internal class AuthorizerDelegate<E : Any, P : PermissionConfig>(
    private val builder: RequirementBuilder<E, P>
) : Authorizer<E, P> {
    context(config: P)
    override fun verify(entity: E, actor: Actor, action: Action): Boolean {
        val requirementContext = RequirementContextImpl(action, entity, actor)
        builder.invoke(requirementContext, config)
        return requirementContext.result()
    }
}

internal class ContextualAuthorizerDelegate<E, P : PermissionConfig, C>(
    private val builder: ContextualRequirementBuilder<E, P, C>
) : ContextualAuthorizer<E, P, C> {
    context(config: P)
    override fun verify(entity: E, actor: Actor, action: Action, context: C): Boolean {
        val requirementContext = RequirementContextImpl(action, entity, actor)
        builder.invoke(requirementContext, config, context)
        return requirementContext.result()
    }
}
