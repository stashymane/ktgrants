package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.SubjectProvider

public interface Actor : SubjectProvider, PermissionOwner
