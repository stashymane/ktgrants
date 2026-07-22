package dev.stashy.ktgrants.permissions.impl.resolvers

import dev.stashy.ktgrants.annotations.KtgrantDsl
import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.Scope
import dev.stashy.ktgrants.permissions.Subject

internal class WildcardChecker(
    private val config: WildcardConfig = WildcardConfig()
) : Resolver.Checker {
    override fun expand(permission: Sequence<Permission>): Sequence<Permission> =
        permission.flatMap { perm -> config.expand(perm) }
}

@KtgrantDsl
public class WildcardConfig {
    public var scope: Boolean = false
    public var subject: Boolean = true
    public var grant: Boolean = false

    internal fun expand(permission: Permission): Sequence<Permission> = sequence {
        if (!scope && !subject && !grant) {
            yield(permission)
            return@sequence
        }

        fun flagSequence(value: Boolean): Sequence<Boolean> =
            if (value) sequenceOf(false, true) else sequenceOf(false)

        // ordered intentionally - the most variable field (subject) first
        for (anyScope in flagSequence(scope)) {
            for (anyGrant in flagSequence(grant)) {
                for (anySubject in flagSequence(subject)) {
                    if (!anyScope && !anySubject && !anyGrant) {
                        yield(permission)
                        continue
                    }

                    yield(
                        permission.copy(
                            scope = if (anyScope) Scope.Any else permission.scope,
                            subject = if (anySubject) Subject.Any else permission.subject,
                            grant = if (anyGrant) Grant.Any else permission.grant
                        )
                    )
                }
            }
        }
    }
}
