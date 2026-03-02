package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.data.PermissionContainer
import dev.stashy.ktgrants.permissions.data.Subject

public interface Actor : PermissionContainer, Subject.Provider {
    
}
