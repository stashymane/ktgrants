package model

import System
import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.PermissionPolicy
import dev.stashy.ktgrants.permissions.Scope
import dev.stashy.ktgrants.permissions.api.PermissionConfig
import dev.stashy.ktgrants.permissions.config.provides

// the permission model that will handle permission resolution
object PermissionModel : PermissionConfig {
    val Read by Grant
    val Write by Grant
    val Create by Grant
    val Delete by Grant

    object Role {
        val Admin by Grant
    }

    override val policy: PermissionPolicy = policyOf {
        defaults = setOf(Read any System)

        grants {
            Role.Admin provides setOf(Read, Write, Create, Delete)
        }

        scopes {
            System provides setOf(Scope("bar"))
        }

        wildcard {
            grant = true
        }
    }
}
