package dev.stashy.ktgrants.permissions.impl.processors

import dev.stashy.ktgrants.permissions.Permission

internal class DefaultsProcessor(
    private val defaults: Set<Permission>
) : PermissionProcessor {
    override fun process(permissions: Sequence<Permission>): Sequence<Permission> =
        defaults.asSequence() + permissions
}
