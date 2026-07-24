package dev.stashy.ktgrants.authorization.dsl

import dev.stashy.ktgrants.authorization.Action
import dev.stashy.ktgrants.authorization.Requirement
import dev.stashy.ktgrants.authorization.RequirementContext
import dev.stashy.ktgrants.authorization.api.Actor

public interface AuthDsl {
    public companion object : AuthDsl

    context(ctx: RequirementContext<E>)
    public val <E> action: Action get() = ctx.action

    context(ctx: RequirementContext<E>)
    public val <E> target: E get() = ctx.target

    context(ctx: RequirementContext<E>)
    public val <E> actor: Actor get() = ctx.actor

    context(ctx: RequirementContext<E>)
    public fun <E> ensure(requirement: Requirement): Unit = ctx.ensure(requirement)

    context(ctx: RequirementContext<E>)
    public fun <E> ensure(check: () -> Boolean): Unit = ctx.ensure(check)

    context(ctx: RequirementContext<E>)
    public fun <E> deny(): Unit = ctx.deny()

    context(ctx: RequirementContext<E>)
    public fun <E> on(actions: Set<Action>, run: () -> Unit): Unit = ctx.on(actions, run)

    context(ctx: RequirementContext<E>)
    public fun <E> on(action: Action, run: () -> Unit): Unit = ctx.on(action, run)
}
