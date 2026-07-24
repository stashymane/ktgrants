package authorization

import dev.stashy.ktgrants.authorization.Action
import dev.stashy.ktgrants.authorization.api.AuthConfig
import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.PermissionConfig
import dev.stashy.ktgrants.permissions.PermissionPolicy

object AuthModel : PermissionConfig, AuthConfig {
    val Read by Grant
    val Write by Grant
    val Delete by Grant

    val Reading by Action
    val Writing by Action
    val Creating by Action
    val Deleting by Action

    override val policy: PermissionPolicy = policyOf()
}
