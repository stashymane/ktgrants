package dev.stashy.ktgrants.authorization.api

import dev.stashy.ktgrants.permissions.PermissionConfig
import kotlin.jvm.JvmInline

public interface Authorized<E : Authorized<E, P>, P : PermissionConfig> {
    public val authorizer: Authorizer<E, P>

    public companion object {
        public fun <E : Authorized<E, P>, P : PermissionConfig> by(
            authorizer: Authorizer<E, P>
        ): Authorized<E, P> = AuthorizedDelegate(authorizer)
    }
}

@JvmInline
internal value class AuthorizedDelegate<E : Authorized<E, P>, P : PermissionConfig>(
    override val authorizer: Authorizer<E, P>
) : Authorized<E, P>
