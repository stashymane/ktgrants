package dev.stashy.ktgrants.permissions.resolvers

import dev.stashy.ktgrants.permissions.Permission

internal interface Resolver {
    fun interface Generator {
        fun process(permissions: Sequence<Permission>): Sequence<Permission>
    }

    fun interface Checker {
        fun expand(permission: Sequence<Permission>): Sequence<Permission>
    }
}
