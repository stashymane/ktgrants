package dev.stashy.ktgrants.authorization

import dev.stashy.ktgrants.util.pascalToKebab
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Serializable
@JvmInline
public value class Action(public val name: String) {
    /**
     * Delegate to create an [Action] based on the delegated field name.
     * The generated action name is in kebab-case.
     *
     * ```
     * object Model: PermissionConfig {
     *     val Reading by Action // equivalent to Action("reading")
     *     val DoingSomething by Action // equivalent to Action("doing-something")
     * }
     * ```
     */
    public companion object : PropertyDelegateProvider<Any, ReadOnlyProperty<Any, Action>> {
        override fun provideDelegate(
            thisRef: Any,
            property: KProperty<*>
        ): ReadOnlyProperty<Any, Action> {
            val name = property.name.pascalToKebab()
            require(name.isNotBlank()) { "Grant name cannot be blank" }
            val action = Action(name)

            return ReadOnlyProperty { _, _ ->
                action
            }
        }
    }
}

public infix fun Action.or(other: Action): Set<Action> = setOf(this, other)

public infix fun Set<Action>.or(other: Action): Set<Action> = this + other
