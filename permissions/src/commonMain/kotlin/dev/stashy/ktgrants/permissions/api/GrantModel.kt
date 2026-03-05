package dev.stashy.ktgrants.permissions.api

import dev.stashy.ktgrants.permissions.Grant
import dev.stashy.ktgrants.permissions.pascalToKebab
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public interface GrantModel {
    /**
     * Generates a grant based on the property name.
     *
     * ```
     * object Grants : GrantModel {
     *      val Read by grant() // <- resolves to Grant("read")
     *      val AdminPanel by grant() // <- resolves to Grant("admin_panel")
     * }
     * ```
     */
    public fun grant(): PropertyDelegateProvider<Any, ReadOnlyProperty<Any, Grant>> = GrantPropertyDelegate
}

private object GrantPropertyDelegate : PropertyDelegateProvider<Any, ReadOnlyProperty<Any, Grant>> {
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
