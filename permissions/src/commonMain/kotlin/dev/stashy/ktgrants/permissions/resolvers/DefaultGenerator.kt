package dev.stashy.ktgrants.permissions.resolvers

import dev.stashy.ktgrants.permissions.Permission

internal class DefaultGenerator(
    private val defaults: Set<Permission>
) : Resolver.Generator {
    override fun process(permissions: Sequence<Permission>): Sequence<Permission> =
        defaults.asSequence() + permissions
}
