package dev.stashy.ktgrants.authorization

import dev.stashy.ktgrants.authorization.api.Actor

public fun interface Requirement {
    public fun evaluate(): Result

    public sealed interface Result {
        public object Allowed : Result
        public class Denied(public val reason: String? = null) : Result
    }
}

public typealias RequirementBuilder<E, P> = context(RequirementContext<E>) P.() -> Unit
public typealias ContextualRequirementBuilder<E, P, C> = context(RequirementContext<E>) P.(C) -> Unit

public sealed interface RequirementContext<E> {
    public val action: Action
    public val target: E
    public val actor: Actor

    public fun ensure(requirement: Requirement)
    public fun ensure(check: () -> Boolean)
    public fun deny(): Unit = ensure { false }

    public fun on(actions: Set<Action>, run: () -> Unit) {
        if (this.action in actions) run()
    }

    public fun on(action: Action, run: () -> Unit): Unit = on(setOf(action), run)
}

internal class RequirementContextImpl<E>(
    override val action: Action,
    override val target: E,
    override val actor: Actor
) : RequirementContext<E> {
    private val results: MutableList<Requirement.Result> = mutableListOf()

    override fun ensure(requirement: Requirement) {
        results.add(requirement.evaluate())
    }

    override fun ensure(check: () -> Boolean) {
        val result = check.invoke()
        results.add(
            if (result) Requirement.Result.Allowed else Requirement.Result.Denied()
        )
    }

    internal fun result(): Boolean {
        return results.isNotEmpty() && results.all { it == Requirement.Result.Allowed }
    }
}
