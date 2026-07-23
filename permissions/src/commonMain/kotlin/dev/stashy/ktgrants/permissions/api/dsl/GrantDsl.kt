package dev.stashy.ktgrants.permissions.api.dsl

import dev.stashy.ktgrants.permissions.*

public interface GrantDsl {
    /**
     * Creates a permission that provides [this] grant for the scope and subject of the [target].
     */
    public infix fun <T> Grant.on(target: T): Permission where T : ScopeProvider, T : SubjectProvider =
        Permission(target.toScope(), target.toSubject(), this)

    /**
     * Creates a permission that provides [this] grant for **any** subject scoped under the [target]'s scope.
     */
    public infix fun Grant.any(target: ScopeProvider): Permission =
        Permission(target.toScope(), Subject.Any, this)

    public companion object : GrantDsl
}
