package dev.stashy.ktgrants.ksp.data

internal data class GrantObjectMetadata(
    val objectName: String,
    val objectPackage: String,
    val objectQualifiedName: String
)
