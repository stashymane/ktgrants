package dev.stashy.ktgrants.permissions.resolvers

import dev.stashy.ktgrants.annotations.KtgrantDsl
import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.Group
import dev.stashy.ktgrants.permissions.Permission
import dev.stashy.ktgrants.permissions.Subject

internal class WildcardChecker(
    private val config: WildcardConfig = WildcardConfig()
) : Resolver.Checker {
    override fun expand(permission: Sequence<Permission>): Sequence<Permission> =
        permission.flatMap { perm -> config.expand(perm) }
}

@KtgrantDsl
public class WildcardConfig {
    public var group: Boolean = false
    public var subject: Boolean = true
    public var grant: Boolean = false

    internal fun expand(permission: Permission): Sequence<Permission> = sequence {
        if (!group && !subject && !grant) {
            yield(permission)
            return@sequence
        }

        fun flagSequence(value: Boolean): Sequence<Boolean> =
            if (value) sequenceOf(false, true) else sequenceOf(false)

        // ordered intentionally - the most variable field (subject) first
        for (anyGroup in flagSequence(group)) {
            for (anyGrant in flagSequence(grant)) {
                for (anySubject in flagSequence(subject)) {
                    if (!anyGroup && !anySubject && !anyGrant) {
                        yield(permission)
                        continue
                    }

                    yield(
                        permission.copy(
                            group = if (anyGroup) Group.Any else permission.group,
                            subject = if (anySubject) Subject.Any else permission.subject,
                            grant = if (anyGrant) Grant.Any else permission.grant
                        )
                    )
                }
            }
        }
    }
}
