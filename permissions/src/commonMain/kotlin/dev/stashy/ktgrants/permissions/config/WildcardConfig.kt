package dev.stashy.ktgrants.permissions.config

import dev.stashy.ktgrants.annotations.KtgrantDsl

@KtgrantDsl
public class WildcardConfig {
    public var scope: Boolean = false
    public var subject: Boolean = true
    public var grant: Boolean = false
}
