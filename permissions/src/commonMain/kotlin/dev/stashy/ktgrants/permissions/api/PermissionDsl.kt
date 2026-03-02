package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.data.Grant
import dev.stashy.ktgrants.permissions.data.Group
import dev.stashy.ktgrants.permissions.data.Permission
import dev.stashy.ktgrants.permissions.data.Subject

public interface PermissionDsl {
    public infix fun Grant.on(target: Permissible.Entity): Permission = Permission(target.group, target.subject, this)
    public infix fun Grant.any(target: Permissible.Type): Permission = Permission(target.group, Subject.Any, this)
    public infix fun Grant.any(target: Group): Permission = Permission(target, Subject.Any, this)

    public companion object : PermissionDsl
}
