package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Group
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.SubjectProvider

public sealed interface Permissible {
    public interface Entity : Permissible {
        public val group: Group
        public val subject: Subject

        private data class Snapshot(override val group: Group, override val subject: Subject) : Entity

        public companion object {
            public operator fun invoke(group: Group, subject: Subject): Entity =
                Snapshot(group, subject)

            public operator fun invoke(group: Group, subjectProvider: SubjectProvider): Entity =
                Snapshot(group, subjectProvider.toSubject())
        }
    }

    public interface Type : Permissible {
        public val group: Group

        public fun withSubject(subject: Subject): Entity = Entity(group, subject)
        public fun withSubject(subject: SubjectProvider): Entity = withSubject(subject.toSubject())
        public fun withSubject(subject: String): Entity = withSubject(Subject(subject))

        private data class Snapshot(override val group: Group) : Type

        public companion object {
            public operator fun invoke(group: Group): Type = Snapshot(group)
        }
    }

    public companion object {
        public inline operator fun <reified T> invoke(subjectProvider: SubjectProvider): Entity =
            Entity(Group(T::class), subjectProvider.toSubject())

        public inline operator fun <reified T> invoke(): Type =
            Type(Group(T::class))
    }
}
