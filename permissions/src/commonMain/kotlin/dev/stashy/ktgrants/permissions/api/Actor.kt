package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.PermissionContainer
import dev.stashy.ktgrants.permissions.Subject

public interface Actor : PermissionContainer, Subject.Provider {

}
