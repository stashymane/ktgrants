package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.authorization.Action
import dev.stashy.ktgrants.authorization.ContextualRequirementBuilder
import dev.stashy.ktgrants.authorization.RequirementBuilder
import dev.stashy.ktgrants.authorization.RequirementContextImpl
import dev.stashy.ktgrants.permissions.PermissionConfig

public interface Actionable<E, P : PermissionConfig> {
    context(config: P)
    public fun verifyAction(entity: E, actor: Actor, action: Action): Boolean

    public companion object {
        public operator fun <E, P : PermissionConfig> invoke(
            builder: RequirementBuilder<E, P>
        ): Actionable<E, P> = ActionableDelegate(builder)
    }
}

public interface ContextualActionable<E, P : PermissionConfig, C> {
    context(config: P)
    public fun verifyAction(entity: E, actor: Actor, action: Action, context: C): Boolean

    public companion object {
        public operator fun <E, P : PermissionConfig, C> invoke(
            builder: ContextualRequirementBuilder<E, P, C>
        ): ContextualActionable<E, P, C> =
            ContextualActionableDelegate(builder)
    }
}

internal class ActionableDelegate<E, P : PermissionConfig>(
    private val builder: RequirementBuilder<E, P>
) : Actionable<E, P> {
    context(config: P)
    override fun verifyAction(entity: E, actor: Actor, action: Action): Boolean {
        val requirementContext = RequirementContextImpl(action, entity, actor)
        builder.invoke(requirementContext, config)
        return requirementContext.result()
    }
}

internal class ContextualActionableDelegate<E, P : PermissionConfig, C>(
    private val builder: ContextualRequirementBuilder<E, P, C>
) : ContextualActionable<E, P, C> {
    context(config: P)
    override fun verifyAction(entity: E, actor: Actor, action: Action, context: C): Boolean {
        val requirementContext = RequirementContextImpl(action, entity, actor)
        builder.invoke(requirementContext, config, context)
        return requirementContext.result()
    }
}
