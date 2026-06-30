package dev.stashy.ktgrants.permissions

import dev.stashy.ktgrants.permissions.Permission.Companion.parse
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Serializable(with = PermissionSerializer::class)
public data class Permission(
    val scope: Scope,
    val subject: Subject,
    val grant: Grant
) {
    public companion object {
        private const val DELIMITER = ':'

        /**
         * Parses a string representation of a permission.
         * Must be formatted as `scope:subject:grant`.
         * @see asString
         */
        public fun parse(string: String): Permission {
            val components = string.split(DELIMITER, limit = 3)
            require(components.size == 3) { "Permission \"$string\" must have 3 components, provided ${components.size}" }

            val scope = Scope(components[0])
            val subject = Subject(components[1])
            val grant = Grant(components[2])

            return Permission(scope, subject, grant)
        }
    }

    /**
     * Returns a formatted string representation of this permission.
     * This representation is valid to use in [parse].
     */
    public fun asString(): String = "${scope.value}$DELIMITER${subject.value}$DELIMITER${grant.value}"

    override fun toString(): String = "Permission(${asString()})"
}

@Serializable
@JvmInline
public value class Scope(public val value: String) : ScopeProvider {
    public constructor(type: KClass<*>) : this(type.simpleName!!.pascalToKebab())

    init {
        verifyValue(value) { "Scope contains invalid characters." }
    }

    override fun toScope(): Scope = this

    public companion object {
        public val Any: Scope = Scope("*")
    }
}

public fun interface ScopeProvider {
    public fun toScope(): Scope
}

@Serializable
@JvmInline
public value class Subject(public val value: String) : SubjectProvider {
    init {
        verifyValue(value) { "Subject contains invalid characters." }
    }

    override fun toSubject(): Subject = this

    public companion object {
        public val Any: Subject = Subject("*")
    }
}

public fun interface SubjectProvider {
    public fun toSubject(): Subject
}

@Serializable
@JvmInline
public value class Grant(public val value: String) : GrantProvider {
    init {
        verifyValue(value) { "Grant contains invalid characters." }
    }

    override fun toGrant(): Grant = this

    public companion object : PropertyDelegateProvider<Any, ReadOnlyProperty<Any, Grant>> {
        public val Any: Grant = Grant("*")

        override fun provideDelegate(
            thisRef: Any,
            property: KProperty<*>
        ): ReadOnlyProperty<Any, Grant> {
            val name = property.name.pascalToKebab()
            require(name.isNotBlank()) { "Grant name cannot be blank" }
            val grant = Grant(name)

            return ReadOnlyProperty { _, _ ->
                grant
            }
        }
    }
}

public fun interface GrantProvider {
    public fun toGrant(): Grant
}
