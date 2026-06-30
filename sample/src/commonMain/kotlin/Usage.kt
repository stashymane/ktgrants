import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.Permissible
import dev.stashy.ktgrants.permissions.api.dsl.GrantDsl.Companion.on
import dev.stashy.ktgrants.permissions.api.hasPermission
import dev.stashy.ktgrants.permissions.api.permission
import model.*

// some example data
object System : Permissible.Type by Permissible<System>()

val user = User(
    id = Id("user-1"),
    permissions = setOf(
        PermissionModel.permission { Read any Foo }
    )
)

// DSL via context
fun contextService() = context(PermissionModel) {
    val foo = Foo(id = Id("bar"), content = "baz")
    val userContext = UserContext(user)

    if (userContext.hasPermission { Read on foo }) {
        // do things
    } else {
        throw IllegalArgumentException("User ${user.id} does not have permission to read ${foo.id}")
    }
}

// without context
fun simpleService() {
    val systemId = "dev1"
    val actor = PermissionModel.actor(
        Id("actor"),
        setOf(PermissionModel.Role.Admin on System.withSubject(systemId))
    )
    val permission = Permission(System.scope, Subject(systemId), PermissionModel.Write)

    if (actor.hasPermission(permission)) {
        // write
    } else {
        throw IllegalArgumentException("Permission ${permission.asString()} missing")
    }
}
