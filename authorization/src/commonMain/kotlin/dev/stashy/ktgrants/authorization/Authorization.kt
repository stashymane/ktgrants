package dev.stashy.ktgrants.authorization

public data class Authorization<out E>(
    val action: Action,
    val entity: E,
)
