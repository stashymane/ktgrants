package model

import dev.stashy.ktgrants.annotations.GrantObject
import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.PermissionModel

object AppModel : PermissionModel by PermissionModel.build({
    graph {
        Grants.Role.Admin provides setOf(Grants.Read, Grants.Write, Grants.Create, Grants.Delete)
    }
})

@GrantObject
object Grants {
    val Read = Grant("read")
    val Write = Grant("write")
    val Create = Grant("create")
    val Delete = Grant("delete")

    public object Role {
        val Admin = Grant("admin")
        val Owner = Grant("owner")
    }
}
