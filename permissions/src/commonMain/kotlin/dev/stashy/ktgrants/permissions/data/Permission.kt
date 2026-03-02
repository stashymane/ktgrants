package dev.stashy.ktgrants.permissions.data

import dev.stashy.ktgrants.permissions.data.Permission.Companion.parse
import dev.stashy.ktgrants.permissions.pascalToKebab
import dev.stashy.ktgrants.permissions.verifyValue
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

@Serializable
public data class Permission(
    val group: Group,
    val subject: Subject,
    val grant: Grant
) {
    public companion object {
        private const val DELIMITER = ':'

        /**
         * Parses a string representation of a permission.
         * Must be formatted as `group:subject:grant`.
         * @see asString
         */
        public fun parse(string: String): Permission {
            val components = string.split(DELIMITER)
            require(components.size == 3) { "Permission \"$string\" must have 3 components, provided ${components.size}" }

            val group = Group(components[0])
            val subject = Subject(components[1])
            val grant = Grant(components[2])

            return Permission(group, subject, grant)
        }
    }

    /**
     * Returns a formatted string representation of this permission.
     * This representation is valid to use in [parse].
     */
    public fun asString(): String = "${group.value}$DELIMITER${subject.value}$DELIMITER${grant.value}"

    override fun toString(): String = "Permission(${asString()})"
}

@Serializable
@JvmInline
public value class Group(public val value: String) {
    public constructor(type: KClass<*>) : this(type.simpleName!!.pascalToKebab())

    public companion object {
        public val Any: Group = Group("*")
    }

    init {
        verifyValue(value) { "Group contains invalid characters." }
    }
}

@Serializable
@JvmInline
public value class Subject(public val value: String) {
    public companion object {
        public val Any: Subject = Subject("*")
    }

    init {
        verifyValue(value) { "Subject contains invalid characters." }
    }

    public fun interface Provider {
        public fun toSubject(): Subject
    }
}

@Serializable
@JvmInline
public value class Grant(public val value: String) {
    public companion object {
        public val Any: Grant = Grant("*")
    }

    init {
        verifyValue(value) { "Grant contains invalid characters." }
    }
}
