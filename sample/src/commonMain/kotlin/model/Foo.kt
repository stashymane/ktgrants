package model

import dev.stashy.ktgrants.authorization.api.Authorized
import dev.stashy.ktgrants.authorization.api.Authorizer
import dev.stashy.ktgrants.authorization.or
import dev.stashy.ktgrants.permissions.api.Permissible
import dev.stashy.ktgrants.permissions.api.hasPermission

private object FooAuthorizer : Authorizer<Foo, PermissionModel> by Authorizer({
    on(Reading or Creating) {
        ensure { actor.hasPermission { Read on target } }
    }
    on(Writing) {
        ensure { actor.hasPermission { Write on target } }
    }
})

data class Foo(
    val id: Id,
    val content: String
) : Permissible.Entity by Foo.withSubject(id),
    Authorized<Foo, PermissionModel> by Authorized.by(FooAuthorizer) {
    companion object : Permissible.Type by Permissible.Type<Foo>()
}
