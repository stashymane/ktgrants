package dev.stashy.ktgrants.permissions.data

public fun interface PermissionContainer {
    /**
     * Returns true if the [permission] is included in this container.
     */
    public infix fun includes(permission: Permission): Boolean
}

internal class PermissionSet
private constructor(
    private val backing: Set<Permission>
) : PermissionContainer {
    constructor(permissions: Sequence<Permission>) : this(permissions.toHashSet())

    override fun includes(permission: Permission): Boolean = backing.contains(permission)
}
