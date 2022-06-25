package bot.myra.diskord.common.cache

import bot.myra.diskord.gateway.events.EventListener

data class DoubleKey<F, S>(val first: F, val second: S)

abstract class GenericCachePolicy<K, V>(
    internal var view: ViewCache<V>? = null,
    internal var get: GetCache<K, V>? = null,
    internal var update: UpdateCache<V>? = null,
    internal var remove: ((K) -> Unit)? = null
) : EventListener {
    fun view(action: ViewCache<V>) = run { view = action }
    fun get(action: GetCache<K, V>) = run { get = action }
    fun update(action: UpdateCache<V>) = run { update = action }
    fun remove(action: RemoveCache<K>) = run { remove = action }

    fun view() = view?.invoke() ?: emptyList()
    fun get(key: K): V? = get?.invoke(key)
    fun update(value: V) = update?.invoke(value)
    fun remove(key: K) = remove?.invoke(key)
}