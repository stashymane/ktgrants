import dev.stashy.ktgrants.authorization.api.authorize
import dev.stashy.ktgrants.permissions.permission
import model.*

// action-based authorization via context
context(_: PermissionModel)
fun authSample() {
    val foo = Foo(id = Id("bar"), content = "baz")
    val user = User(
        id = Id("foo"),
        permissions = setOf(permission { Read any Foo })
    )
    val userContext = UserContext(user)

    if (userContext.authorize { Reading the foo }) {
        // read foo
    } else {
        throw IllegalArgumentException("User ${user.id} is not allowed to read ${foo.id}")
    }
}
