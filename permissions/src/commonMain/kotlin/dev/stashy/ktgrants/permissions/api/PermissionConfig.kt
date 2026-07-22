package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionPolicy
import dev.stashy.ktgrants.permissions.PermissionPolicyBuilder
import dev.stashy.ktgrants.permissions.SubjectProvider
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl
import kotlin.jvm.JvmName

public interface PermissionConfig : GrantDsl {
    public val policy: PermissionPolicy

    public fun actor(subject: SubjectProvider, permissions: Set<Permission>): Actor =
        Actor.create(policy, subject, permissions)

    public fun policyOf(builder: PermissionPolicyBuilder.() -> Unit): PermissionPolicy =
        PermissionPolicy.build(builder)
}

@JvmName("permissionBuilderExtension")
public inline fun <T : PermissionConfig> T.permission(builder: T.() -> Permission): Permission = builder()

context(config: T)
public inline fun <T : PermissionConfig> permission(builder: T.() -> Permission): Permission =
    config.permission(builder)
