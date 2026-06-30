package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.SubjectProvider
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl
import kotlin.jvm.JvmName

public interface PermissionConfig : GrantModel, GrantDsl {
    public val resolver: PermissionResolver

    public fun actor(subject: SubjectProvider, permissions: Set<Permission>): Actor =
        Actor.create(resolver, subject, permissions)
}

@JvmName("permissionBuilderExtension")
public inline fun <T : PermissionConfig> T.permission(builder: T.() -> Permission): Permission = builder()

context(config: T)
public inline fun <T : PermissionConfig> permission(builder: T.() -> Permission): Permission =
    config.permission(builder)

context(config: T)
public inline fun <T : PermissionConfig> Actor.hasPermission(builder: T.() -> Permission): Boolean =
    this.hasPermission(builder(config))
