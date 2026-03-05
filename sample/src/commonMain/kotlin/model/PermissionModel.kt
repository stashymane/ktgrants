package model

import dev.stashy.ktgrants.annotations.GrantObject
import dev.stashy.ktgrants.permissions.PermissionModel
import dev.stashy.ktgrants.permissions.api.GrantModel

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
object Grants : GrantModel {
    val Read by grant()
    val Write by grant()
    val Create by grant()
    val Delete by grant()

    object Role {
        val Admin by grant()
        val Owner by grant()
    }
}
