package model

import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.PermissionPolicy
import dev.stashy.ktgrants.permissions.api.PermissionConfig

// the permission model that will handle permission resolution
object PermissionModel : PermissionConfig {
    val Read by Grant
    val Write by Grant
    val Create by Grant
    val Delete by Grant

    object Role {
        val Admin by Grant
        val Owner by Grant
    }

    override val policy: PermissionPolicy = policyOf {
        graph {
            Role.Admin provides setOf(Read, Write, Create, Delete)
        }
        wildcard {
            grant = true
        }
    }
}
