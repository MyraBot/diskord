package diskord.common.caching

import diskord.gateway.listeners.EventListener

abstract class DoubleKeyCache<F, S, V> : EventListener() {
    internal val map: MutableMap<Key<F, S>, V> = mutableMapOf()
    val size: Int get() = map.size

    operator fun get(firstKey: F, secondKey: S): V? {
        val key = Key(firstKey, secondKey)
        return map.getOrElse(key) {
            retrieve(firstKey, secondKey)?.also { map[key] = it }
        }
    }

    abstract fun retrieve(firstKey: F, secondKey: S): V?
    abstract fun update(firstKey: F, secondKey: S, value: V)

    data class Key<F, S>(val first: F, val second: S)
}