package dev.stashy.ktgrants.permissions.api.dsl

import dev.stashy.ktgrants.permissions.*

public interface GrantDsl {
    public infix fun <T> Grant.on(target: T): Permission where T : ScopeProvider, T : SubjectProvider =
        Permission(target.toScope(), target.toSubject(), this)

    public infix fun Grant.any(target: ScopeProvider): Permission =
        Permission(target.toScope(), Subject.Any, this)

    public companion object : GrantDsl
}
