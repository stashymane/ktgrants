package dev.stashy.ktgrants.permissions

import dev.stashy.ktgrants.permissions.config.PermissionPolicyBuilder
import dev.stashy.ktgrants.permissions.config.PermissionPolicyBuilderImpl

/**
 * A permission policy that transforms a simple set of permissions to a [PermissionCollection],
 * which provides support for additional checks, such as wildcards, dependencies, etc.
 */
public interface PermissionPolicy {
    public fun process(permissions: Sequence<Permission>): PermissionCollection
    public fun process(permissions: Set<Permission>): PermissionCollection = process(permissions.asSequence())

    public companion object {
        public fun build(builder: PermissionPolicyBuilder.() -> Unit): PermissionPolicy =
            PermissionPolicyBuilderImpl().apply(builder).build()
    }
}
