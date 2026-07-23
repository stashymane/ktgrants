package dev.stashy.ktgrants.permissions.config

import dev.stashy.ktgrants.annotations.KtgrantDsl
import dev.stashy.ktgrants.permissions.*
import dev.stashy.ktgrants.permissions.impl.processors.DefaultsProcessor
import dev.stashy.ktgrants.permissions.impl.processors.PermissionProcessor
import dev.stashy.ktgrants.permissions.impl.tree.*

/**
 * Builder for the default permission policy.
 */
@KtgrantDsl
public sealed interface PermissionPolicyBuilder {
    /**
     * The default permission set granted to everyone.
     */
    public var defaults: Set<Permission>

    /**
     * Configures [Scope] graph relationships.
     *
     * ```
     * scopes {
     *     Scope("system") provides setOf(Scope("posts"), Scope("comments"))
     * }
     * ```
     */
    public fun scopes(builder: context(GraphConfig<Scope>) () -> Unit)

    /**
     * Configures [Subject] graph relationships.
     *
     * ```
     * subjects {
     *     Subject("john") provides setOf(Subject("bob"), Subject("steve"))
     * }
     * ```
     */
    public fun subjects(builder: context(GraphConfig<Subject>) () -> Unit)

    /**
     * Configures [Grant] graph relationships.
     *
     * ```
     * grants {
     *     Grant("admin") provides setOf(Grant("read"), Grant("write"))
     * }
     * ```
     */
    public fun grants(builder: context(GraphConfig<Grant>) () -> Unit)

    /**
     * Configures wildcard support for this policy.
     * By default, only [Subject]s have wildcards enabled.
     */
    public fun wildcard(config: WildcardConfig.() -> Unit)
}

internal class PermissionPolicyBuilderImpl : PermissionPolicyBuilder {
    override var defaults: Set<Permission> = emptySet()

    private var scopeGraph: GraphConfigImpl<Scope>? = null
    override fun scopes(builder: GraphConfig<Scope>.() -> Unit) {
        require(scopeGraph == null) { "Scope graph has already been configured" }
        scopeGraph = GraphConfigImpl<Scope>().apply(builder)
    }

    private var subjectGraph: GraphConfigImpl<Subject>? = null
    override fun subjects(builder: GraphConfig<Subject>.() -> Unit) {
        require(subjectGraph == null) { "Subject graph has already been configured" }
        subjectGraph = GraphConfigImpl<Subject>().apply(builder)
    }

    private var grantGraph: GraphConfigImpl<Grant>? = null
    override fun grants(builder: GraphConfig<Grant>.() -> Unit) {
        require(grantGraph == null) { "Grant graph has already been configured" }
        grantGraph = GraphConfigImpl<Grant>().apply(builder)
    }

    private var wildcardConfig: WildcardConfig? = null
    override fun wildcard(config: WildcardConfig.() -> Unit) {
        require(wildcardConfig == null) { "Wildcards have already been configured" }
        wildcardConfig = WildcardConfig().apply(config)
    }

    internal fun build(): PermissionPolicy {
        val processors = mutableListOf<PermissionProcessor>()
        val wildcardConfig = wildcardConfig ?: WildcardConfig()

        if (defaults.isNotEmpty())
            processors += DefaultsProcessor(defaults)

        var scopeLookup: Lookup<Scope> = BaseLookup()
        if (wildcardConfig.scope)
            scopeLookup = WildcardLookup(Scope.Any, scopeLookup)
        scopeGraph?.let {
            scopeLookup = GraphLookup(it.buildInverted(), scopeLookup)
        }

        var subjectLookup: Lookup<Subject> = BaseLookup()
        if (wildcardConfig.subject)
            subjectLookup = WildcardLookup(Subject.Any, subjectLookup)
        subjectGraph?.let {
            subjectLookup = GraphLookup(it.buildInverted(), subjectLookup)
        }

        var grantLookup: Lookup<Grant> = BaseLookup()
        if (wildcardConfig.grant)
            grantLookup = WildcardLookup(Grant.Any, grantLookup)
        grantGraph?.let {
            grantLookup = GraphLookup(it.buildInverted(), grantLookup)
        }

        return TreeResolver(processors, scopeLookup, subjectLookup, grantLookup)
    }
}
