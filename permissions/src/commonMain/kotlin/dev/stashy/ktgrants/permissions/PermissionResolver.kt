package dev.stashy.ktgrants.permissions

import dev.stashy.ktgrants.annotations.KtgrantDsl
import dev.stashy.ktgrants.permissions.resolvers.*

public interface PermissionResolver {
    public fun process(permissions: Sequence<Permission>): PermissionCollection
    public fun process(permissions: Set<Permission>): PermissionCollection = process(permissions.asSequence())

    public companion object {
        public fun build(builder: PermissionResolverBuilder.() -> Unit): PermissionResolver =
            PermissionResolverBuilder().apply(builder).build()
    }
}

@KtgrantDsl
public class PermissionResolverBuilder internal constructor() {
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

    internal fun build(): PermissionResolver {
        var model = graphConfig?.build() ?: ExplicitResolver()

        if (defaults.isNotEmpty())
            model = ResolverWithDefaults(defaults, model)

        wildcardConfig?.let { wildcardConfig ->
            model = ResolverWithWildcard(model, wildcardConfig)
        }

        return model
    }
}
