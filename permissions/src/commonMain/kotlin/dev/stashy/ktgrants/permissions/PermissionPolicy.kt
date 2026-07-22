package dev.stashy.ktgrants.permissions

import dev.stashy.ktgrants.annotations.KtgrantDsl
import dev.stashy.ktgrants.permissions.impl.PipelineResolver
import dev.stashy.ktgrants.permissions.impl.resolvers.*

public interface PermissionPolicy {
    public fun process(permissions: Sequence<Permission>): PermissionCollection
    public fun process(permissions: Set<Permission>): PermissionCollection = process(permissions.asSequence())

    public companion object {
        public fun build(builder: PermissionPolicyBuilder.() -> Unit): PermissionPolicy =
            PermissionPolicyBuilder().apply(builder).build()
    }
}

@KtgrantDsl
public class PermissionPolicyBuilder internal constructor() {
    public var defaults: Set<Permission> = emptySet()

    private var graphConfig: GraphConfig? = null
    public fun graph(builder: GraphConfig.() -> Unit) {
        require(graphConfig == null) { "Graph has already been configured" }
        graphConfig = GraphConfig().apply(builder)
    }

    private var wildcardConfig: WildcardConfig? = null
    public fun wildcard(config: WildcardConfig.() -> Unit) {
        require(wildcardConfig == null) { "Wildcards have already been configured" }
        wildcardConfig = WildcardConfig().apply(config)
    }

    private var generator: PermissionGenerator? = null
    public fun generator(fn: PermissionGenerator) {
        require(generator == null) { "A custom generator has already been configured" }
        generator = fn
    }

    internal fun build(): PermissionPolicy {
        val generators = mutableListOf<Resolver.Generator>()
        val checkers = mutableListOf<Resolver.Checker>()

        if (defaults.isNotEmpty())
            generators += DefaultGenerator(defaults)

        generator?.let { generator ->
            generators += CustomGenerator(generator)
        }

        graphConfig?.let { graphConfig ->
            generators += graphConfig.build()
        }

        wildcardConfig?.let { wildcardConfig ->
            checkers += WildcardChecker(wildcardConfig)
        }

        return PipelineResolver(generators, checkers)
    }
}
