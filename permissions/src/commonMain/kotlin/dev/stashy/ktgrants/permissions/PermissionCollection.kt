package dev.stashy.ktgrants.permissions

public fun interface PermissionCollection {
    /**
     * Returns true if the [permission] is included in this container.
     */
    public infix fun includes(permission: Permission): Boolean
}

internal class PermissionSet
private constructor(
    private val backing: Set<Permission>
) : PermissionCollection {
    constructor(permissions: Sequence<Permission>) : this(permissions.toHashSet())

    override fun includes(permission: Permission): Boolean = backing.contains(permission)
    override fun toString(): String {
        return "PermissionSet(${backing.joinToString(", ")})"
    }
}
