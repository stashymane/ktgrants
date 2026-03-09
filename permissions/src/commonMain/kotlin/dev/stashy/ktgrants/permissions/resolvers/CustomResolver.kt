package dev.stashy.ktgrants.permissions.resolvers

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection
import dev.stashy.ktgrants.permissions.PermissionResolver

public typealias PermissionGenerator = suspend SequenceScope<Permission>.(Permission) -> Unit

internal class CustomResolver(
    private val wrapped: PermissionResolver,
    private val generator: PermissionGenerator
) : PermissionResolver by wrapped {
    override fun process(permissions: Sequence<Permission>): PermissionCollection {
        val generated = sequence {
            permissions.forEach { permission -> generator(permission) }
        }
        return wrapped.process(generated)
    }
}
