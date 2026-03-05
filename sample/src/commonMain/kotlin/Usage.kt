import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.Permissible
import generated.PermissionContext.on
import generated.hasPermission
import generated.permission
import model.*

// some example data
object System : Permissible.Type by Permissible<System>()

val user = User(
    id = Id("user-1"),
    permissions = setOf(
        permission { Read any Foo }
    )
)

// KSP-generated usage
fun generatedDslService() {
    val foo = Foo(id = Id("bar"), content = "baz")
    val userContext = UserContext(user)

    if (userContext.hasPermission { Read on foo }) {
        // do things
    } else {
        throw IllegalArgumentException("User ${user.id} does not have permission to read ${foo.id}")
    }
}

// without KSP
fun simpleService() {
    val systemId = "dev1"
    val actor = Permissions.actor(
        Id("actor"),
        setOf(Permissions.Role.Admin on System.withSubject(systemId))
    )
    val permission = Permission(System.group, Subject(systemId), Permissions.Write)

    if (actor.hasPermission(permission)) {
        // write
    } else {
        throw IllegalArgumentException("Permission ${permission.asString()} missing")
    }
}
