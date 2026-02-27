package dev.stashy.ktgrants.permissions.data.model

import dev.stashy.ktgrants.KtgrantDsl
import dev.stashy.ktgrants.permissions.data.*

private typealias PermissionMapper = (Permission) -> Permission

internal class ModelWithWildcard(
    private val wrapped: PermissionModel,
    config: WildcardConfig = WildcardConfig()
) : PermissionModel by wrapped {
    private val mappers: List<PermissionMapper> = config.build()

    override fun process(permissions: Sequence<Permission>): PermissionContainer =
        ContainerWrapper(wrapped.process(permissions))

    private inner class ContainerWrapper(
        private val delegate: PermissionContainer
    ) : PermissionContainer {
        override fun includes(permission: Permission): Boolean {
            if (delegate.includes(permission)) return true
            return mappers.any { delegate.includes(it(permission)) }
        }
    }
}

@KtgrantDsl
public class WildcardConfig {
    public var group: Boolean = false
    public var subject: Boolean = false
    public var grant: Boolean = false

    internal fun build(): List<PermissionMapper> = buildList {
        if (!group && !subject && !grant) return@buildList

        fun flagSequence(value: Boolean): Sequence<Boolean> = if (value) sequenceOf(false, true) else sequenceOf(false)

        // ordered intentionally - the most variable field (subject) first
        for (anyGroup in flagSequence(group)) {
            for (anyGrant in flagSequence(grant)) {
                for (anySubject in flagSequence(subject)) {
                    if (!anyGroup && !anySubject && !anyGrant) continue

                    add {
                        it.copy(
                            group = if (anyGroup) Group.Any else it.group,
                            subject = if (anySubject) Subject.Any else it.subject,
                            grant = if (anyGrant) Grant.Any else it.grant
                        )
                    }
                }
            }
        }
    }
}
