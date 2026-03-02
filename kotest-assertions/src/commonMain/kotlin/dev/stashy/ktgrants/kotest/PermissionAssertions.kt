package dev.stashy.ktgrants.kotest

import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.PermissionCollection
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

public fun <T : PermissionCollection> include(allowed: Permission): Matcher<T> = Matcher { provider ->
    MatcherResult(
        provider.includes(allowed),
        { "$provider should include $allowed" },
        { "$provider should not include $allowed" }
    )
}

public infix fun <T : PermissionCollection> T.shouldInclude(other: Permission): T {
    this should include(other)
    return this
}

public infix fun <T : PermissionCollection> T.shouldNotInclude(other: Permission): T {
    this shouldNot include(other)
    return this
}
