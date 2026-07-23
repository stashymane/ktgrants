package dev.stashy.ktgrants.permissions

import dev.stashy.ktgrants.permissions.api.Actor
import dev.stashy.ktgrants.permissions.config.PermissionPolicyBuilder
import dev.stashy.ktgrants.permissions.dsl.GrantDsl
import kotlin.jvm.JvmName

/**
 * The permission configuration of your project.
 * This should be provided via context for downstream users of the permission system.
 *
 * ```
 * object Permissions: PermissionConfig {
 *     val Read by Grant
 *     val Write by Grant
 *
 *     override val policy: PermissionPolicy = policyOf { /* ... */ }
 * }
 * ```
 */
public interface PermissionConfig : GrantDsl {
    /**
     * The policy of your configuration.
     *
     * Configure the default policy using [policyOf].
     * Otherwise, you may implement a custom policy by implementing [PermissionPolicy].
     */
    public val policy: PermissionPolicy

    /**
     * Configures the default [PermissionPolicy].
     */
    public fun policyOf(builder: PermissionPolicyBuilder.() -> Unit): PermissionPolicy =
        PermissionPolicy.build(builder)

    public fun actor(subject: SubjectProvider, permissions: Set<Permission>): Actor =
        Actor.create(policy, subject, permissions)
}

@JvmName("permissionBuilderExtension")
public inline fun <T : PermissionConfig> T.permission(builder: T.() -> Permission): Permission = builder()

context(config: T)
public inline fun <T : PermissionConfig> permission(builder: T.() -> Permission): Permission =
    config.permission(builder)
