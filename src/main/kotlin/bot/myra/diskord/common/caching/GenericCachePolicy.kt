package bot.myra.diskord.common.caching

import bot.myra.diskord.gateway.events.EventListener

data class DoubleKey<F, S>(val first: F, val second: S)

abstract class GenericCachePolicy<K, V>(
    internal var view: (() -> List<V>)? = null,
    internal var get: ((K) -> V?)? = null,
    internal var update: ((V) -> Unit)? = null,
    internal var remove: ((K) -> Unit)? = null
) : EventListener {
    fun view(action: () -> List<V>) = run { view = action }
    fun get(action: (K) -> V?) = run { get = action }
    fun update(action: (V) -> Unit) = run { update = action }
    fun remove(action: (K) -> Unit) = run { remove = action }

    fun view() = view?.invoke() ?: emptyList()
    fun get(key: K): V? = get?.invoke(key)
    fun update(value: V) = update?.invoke(value)
    fun remove(key: K) = remove?.invoke(key)
}