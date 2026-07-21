package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Scope
import dev.stashy.ktgrants.permissions.ScopeProvider
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.SubjectProvider

public sealed interface Permissible {
    public interface Entity : Permissible, ScopeProvider, SubjectProvider {
        public val scope: Scope
        public val subject: Subject

        override fun toScope(): Scope = scope
        override fun toSubject(): Subject = subject

        private data class Snapshot(override val scope: Scope, override val subject: Subject) : Entity

        public companion object {
            public operator fun invoke(scopeProvider: ScopeProvider, subjectProvider: SubjectProvider): Entity =
                Snapshot(scopeProvider.toScope(), subjectProvider.toSubject())
        }
    }
    
    public interface Type : Permissible, ScopeProvider {
        public val scope: Scope

        public fun withSubject(subject: SubjectProvider): Entity = Entity(scope, subject.toSubject())

        override fun toScope(): Scope = scope

        private data class Snapshot(override val scope: Scope) : Type

        public companion object {
            public operator fun invoke(scope: Scope): Type = Snapshot(scope)
            public inline operator fun <reified T> invoke(): Type =
                Type(Scope(T::class))
        }
    }
}
