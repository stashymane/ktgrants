import dev.stashy.ktgrants.authorization.api.Authorizer
import dev.stashy.ktgrants.authorization.or
import dev.stashy.ktgrants.permissions.api.hasPermission
import dev.stashy.ktgrants.permissions.permission
import model.*

// authorization module
val fooAuthorizer = Authorizer<Foo, PermissionModel> {
    on(Reading or Creating) {
        ensure { actor.hasPermission { Read on target } }
    }
    on(Writing) {
        ensure { actor.hasPermission { Write on target } }
    }
}

// action-based authorization via context
context(_: PermissionModel)
fun authSample() {
    val foo = Foo(id = Id("bar"), content = "baz")
    val user = User(
        id = Id("foo"),
        permissions = setOf(permission { Read any Foo })
    )
    val userContext = UserContext(user)

    if (fooAuthorizer.verify(foo, userContext, PermissionModel.Reading)) {
        // read foo
    } else {
        throw IllegalArgumentException("User ${user.id} is not allowed to read ${foo.id}")
    }
}
