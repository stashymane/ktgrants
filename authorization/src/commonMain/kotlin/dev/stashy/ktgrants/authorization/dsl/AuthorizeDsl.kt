package dev.stashy.ktgrants.authorization.dsl

import dev.stashy.ktgrants.authorization.Action
import dev.stashy.ktgrants.authorization.Authorization

public interface AuthorizeDsl {
    /** Creates an authorization request for [this] action against [entity]. */
    public infix fun <E> Action.the(entity: E): Authorization<E> =
        Authorization(this, entity)

    public companion object : AuthorizeDsl
}
