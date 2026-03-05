package model

import dev.stashy.ktgrants.annotations.PermissionObject
import dev.stashy.ktgrants.permissions.PermissionResolver
import dev.stashy.ktgrants.permissions.api.PermissionConfig

// the permission model that will handle permission resolution
// you can even have multiple at once
// objects annotated with `@GrantObject` get generated accessors, like `permission { <grant> on <entity> }`,
// so that you don't have to import each grant separately
@PermissionObject
object Permissions : PermissionConfig {
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
