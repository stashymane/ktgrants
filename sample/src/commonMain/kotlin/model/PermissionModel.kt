package model

import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.api.PermissionConfig

// the permission model that will handle permission resolution
object PermissionModel : PermissionConfig {
    val Read by grant()
    val Write by grant()
    val Create by grant()
    val Delete by grant()

    object Role {
        val Admin by grant()
        val Owner by grant()
    }

    override val resolver: PermissionResolver = PermissionResolver.build {
        graph {
            Role.Admin provides setOf(Read, Write, Create, Delete)
        }
        wildcard {
            grant = true
        }
    }
}
