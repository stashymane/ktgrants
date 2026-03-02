package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.PermissionCollection
import dev.stashy.ktgrants.permissions.Subject

public interface Actor : PermissionCollection, Subject.Provider {

}
