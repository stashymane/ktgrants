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

    private var generator: PermissionGenerator? = null
    public fun generator(fn: PermissionGenerator) {
        require(generator == null) { "A custom generator has already been configured" }
        generator = fn
    }

    internal fun build(): PermissionResolver {
        var model = graphConfig?.build() ?: PermissionSetResolver()

        if (defaults.isNotEmpty())
            model = DefaultResolver(model, defaults)
        
        generator?.let { generator ->
            model = CustomResolver(model, generator)
        }

        wildcardConfig?.let { wildcardConfig ->
            model = WildcardResolver(model, wildcardConfig)
        }

        return model
    }
}
