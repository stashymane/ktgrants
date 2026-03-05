package dev.stashy.ktgrants.permissions

import dev.stashy.ktgrants.permissions.Permission.Companion.parse
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

@Serializable(with = PermissionSerializer::class)
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
            val components = string.split(DELIMITER, limit = 3)
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
public value class Group(public val value: String) : GroupProvider {
    public constructor(type: KClass<*>) : this(type.simpleName!!.pascalToKebab())

    init {
        verifyValue(value) { "Group contains invalid characters." }
    }

    override fun toGroup(): Group = this

    public companion object {
        public val Any: Group = Group("*")
    }
}

public fun interface GroupProvider {
    public fun toGroup(): Group
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

    public companion object {
        public val Any: Grant = Grant("*")
    }
}

public fun interface GrantProvider {
    public fun toGrant(): Grant
}
