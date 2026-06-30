package dev.stashy.ktgrants.permissions.api.dsl

import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.Scope
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.Permissible

public interface GrantDsl {
    public infix fun Grant.on(target: Permissible.Entity): Permission = Permission(target.scope, target.subject, this)
    public infix fun Grant.any(target: Permissible.Type): Permission = Permission(target.scope, Subject.Any, this)
    public infix fun Grant.any(target: Scope): Permission = Permission(target, Subject.Any, this)

    public companion object : GrantDsl
}
