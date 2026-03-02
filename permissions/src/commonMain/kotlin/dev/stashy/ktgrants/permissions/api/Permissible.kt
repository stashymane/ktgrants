package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.data.Group
import dev.stashy.ktgrants.permissions.data.Subject

public sealed interface Permissible {
    public interface Entity : Permissible {
        public val group: Group
        public val subject: Subject

        public data class Snapshot(override val group: Group, override val subject: Subject) : Entity
    }

    public interface Type : Permissible {
        public val group: Group

        public fun withSubject(subject: Subject): Entity = Entity.Snapshot(group, subject)
        public fun withSubject(subject: Subject.Provider): Entity = withSubject(subject.toSubject())
        public fun withSubject(subject: String): Entity = withSubject(Subject(subject))

        public data class Snapshot(override val group: Group) : Type
    }

    public companion object {
        public operator fun invoke(group: Group, subject: Subject): Entity = Entity.Snapshot(group, subject)
        public operator fun invoke(group: Group): Type = Type.Snapshot(group)

        public inline operator fun <reified T> invoke(subjectProvider: Subject.Provider): Entity =
            invoke(Group(T::class), subjectProvider.toSubject())

        public inline operator fun <reified T> invoke(): Type =
            invoke(Group(T::class))
    }
}
