package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Scope
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.SubjectProvider

public sealed interface Permissible {
    public interface Entity : Permissible {
        public val scope: Scope
        public val subject: Subject

        private data class Snapshot(override val scope: Scope, override val subject: Subject) : Entity

        public companion object {
            public operator fun invoke(scope: Scope, subject: Subject): Entity =
                Snapshot(scope, subject)

            public operator fun invoke(scope: Scope, subjectProvider: SubjectProvider): Entity =
                Snapshot(scope, subjectProvider.toSubject())
        }
    }

    public interface Type : Permissible {
        public val scope: Scope

        public fun withSubject(subject: Subject): Entity = Entity(scope, subject)
        public fun withSubject(subject: SubjectProvider): Entity = withSubject(subject.toSubject())
        public fun withSubject(subject: String): Entity = withSubject(Subject(subject))

        private data class Snapshot(override val scope: Scope) : Type

        public companion object {
            public operator fun invoke(scope: Scope): Type = Snapshot(scope)
        }
    }

    public companion object {
        public inline operator fun <reified T> invoke(subjectProvider: SubjectProvider): Entity =
            Entity(Scope(T::class), subjectProvider.toSubject())

        public inline operator fun <reified T> invoke(): Type =
            Type(Scope(T::class))
    }
}
