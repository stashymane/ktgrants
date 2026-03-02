import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.api.Permissible
import generated.PermissionContext.on
import generated.permission
import model.*

val user = User(
    id = Id("user-1"),
    permissions = setOf(
        permission { Read any Foo }
    )
)

fun dslService() {
    val foo = Foo(id = Id("foo"), content = "bar")
    val permission = permission { Read on foo }
    if (user.includes(permission)) {
        // do things
    } else {
        throw IllegalArgumentException("User ${user.id} does not have permission ${permission.asString()}")
    }
}

object System : Permissible.Type by Permissible<System>()

fun simpleService() {
    val systemId = "dev1"
    val userPermissions = AppModel.process(setOf(Grants.Role.Admin on System.withSubject(systemId)))
    val permission = Permission(System.group, Subject(systemId), Grants.Write)

    if (userPermissions.includes(permission)) {
        // write
    } else {
        throw IllegalArgumentException("Permission ${permission.asString()} missing")
    }
}
