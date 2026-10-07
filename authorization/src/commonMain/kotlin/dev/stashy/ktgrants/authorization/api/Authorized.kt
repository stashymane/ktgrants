package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.permissions.PermissionConfig
import kotlin.jvm.JvmInline

public interface ContextuallyAuthorized<E, P : PermissionConfig, C>
        where E : Any, E : ContextuallyAuthorized<E, P, C> {
    public val authorizer: ContextualAuthorizer<E, P, C>

    public companion object {
        public fun <E, P : PermissionConfig, C> by(
            authorizer: ContextualAuthorizer<E, P, C>,
        ): ContextuallyAuthorized<E, P, C>
                where E : ContextuallyAuthorized<E, P, C> =
            ContextuallyAuthorizedDelegate(authorizer)
    }
}

public typealias Authorized<E, P> = ContextuallyAuthorized<E, P, Unit>

@JvmInline
internal value class ContextuallyAuthorizedDelegate<E, P : PermissionConfig, C>(
    override val authorizer: ContextualAuthorizer<E, P, C>,
) : ContextuallyAuthorized<E, P, C>
        where E : ContextuallyAuthorized<E, P, C>
