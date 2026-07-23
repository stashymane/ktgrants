package dev.stashy.ktgrants.permissions.impl.tree

internal sealed interface Lookup<T> {
    fun resolve(requested: T): List<T>
}

internal class BaseLookup<T> : Lookup<T> {
    override fun resolve(requested: T): List<T> = listOf(requested)
}

internal class WildcardLookup<T>(
    private val anyValue: T,
    private val delegate: Lookup<T>
) : Lookup<T> {
    override fun resolve(requested: T): List<T> {
        val resolved = delegate.resolve(requested)
        return if (requested == anyValue) resolved else (resolved + anyValue).distinct()
    }
}

internal class GraphLookup<T>(
    private val invertedGraph: Map<T, Set<T>>,
    private val delegate: Lookup<T>
) : Lookup<T> {
    override fun resolve(requested: T): List<T> {
        return delegate.resolve(requested).flatMap {
            listOf(it) + (invertedGraph[it] ?: emptySet())
        }.distinct()
    }
}
