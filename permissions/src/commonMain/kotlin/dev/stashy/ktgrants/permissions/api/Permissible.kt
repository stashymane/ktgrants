package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.data.Grant
import dev.stashy.ktgrants.permissions.data.Group
import dev.stashy.ktgrants.permissions.data.Permission
import dev.stashy.ktgrants.permissions.data.Subject

public sealed interface Permissible {
    public interface Data : Permissible {
        public val group: Group
        public val subject: Subject

        public data class Snapshot(override val group: Group, override val subject: Subject) : Data
    }

    public interface Meta : Permissible {
        public val group: Group

        public fun withSubject(subject: Subject): Data = Data.Snapshot(group, subject)
        public fun withSubject(subject: Subject.Provider): Data = withSubject(subject.provide())

        public data class Snapshot(override val group: Group) : Meta
    }

    public companion object {
        public operator fun invoke(group: Group, subject: Subject): Data = Data.Snapshot(group, subject)
        public operator fun invoke(group: Group): Meta = Meta.Snapshot(group)

        public inline operator fun <reified T> invoke(subjectProvider: Subject.Provider): Data =
            invoke(Group(T::class), subjectProvider.provide())

        public inline operator fun <reified T> invoke(): Meta =
            invoke(Group(T::class))
    }
}

public infix fun Grant.on(target: Permissible.Data): Permission = Permission(target.group, target.subject, this)
public infix fun Grant.any(target: Permissible.Meta): Permission = Permission(target.group, Subject.Any, this)
