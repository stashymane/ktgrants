package dev.stashy.ktgrants.permissions.impl.processors

import dev.stashy.ktgrants.permissions.Permission

internal fun interface PermissionProcessor {
    fun process(permissions: Sequence<Permission>): Sequence<Permission>
}
