package dev.stashy.ktgrants.permissions.resolvers

import dev.stashy.ktgrants.permissions.Permission

public typealias PermissionGenerator = suspend SequenceScope<Permission>.(Permission) -> Unit

internal class CustomGenerator(
    private val generator: PermissionGenerator
) : Resolver.Generator {
    override fun process(permissions: Sequence<Permission>): Sequence<Permission> = sequence {
        permissions.forEach { permission -> generator(permission) }
    }
}
