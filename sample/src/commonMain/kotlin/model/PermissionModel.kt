package model

import dev.stashy.ktgrants.annotations.GrantObject
import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.PermissionModel

// the permission model that will handle permission resolution
// you can even have multiple at once
object AppModel : PermissionModel by PermissionModel.build({
    graph {
        Grants.Role.Admin provides setOf(Grants.Read, Grants.Write, Grants.Create, Grants.Delete)
    }
    wildcard {
        grant = true
    }
})

// objects with this annotation get generated accessors, like `permission { <grant> on <entity> }`,
// so that you don't have to import each grant separately
@GrantObject
object Grants {
    val Read = Grant("read")
    val Write = Grant("write")
    val Create = Grant("create")
    val Delete = Grant("delete")

    object Role {
        val Admin = Grant("admin")
        val Owner = Grant("owner")
    }
}
