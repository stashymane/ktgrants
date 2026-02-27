package dev.stashy.ktgrants.permissions.data

import dev.stashy.ktgrants.KtgrantDsl
import dev.stashy.ktgrants.permissions.data.model.*

public interface PermissionModel {
    public fun process(permissions: Sequence<Permission>): PermissionContainer

    public companion object {
        public fun build(builder: PermissionModelBuilder.() -> Unit): PermissionModel =
            PermissionModelBuilder().apply(builder).build()
    }
}

@KtgrantDsl
public class PermissionModelBuilder internal constructor() {
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

    internal fun build(): PermissionModel {
        var model = graphConfig?.build() ?: ExplicitModel()

        if (defaults.isNotEmpty())
            model = ModelWithDefaults(defaults, model)

        wildcardConfig?.let { wildcardConfig ->
            model = ModelWithWildcard(model, wildcardConfig)
        }

        return model
    }
}
