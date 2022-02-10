package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.rest.request.promises.Promise

data class DoubleKey(val first: String, val second: String)

abstract class Cache<K, V>(
    var retrieve: (K) -> Promise<V> = { Promise.of(null) },
) : EventListener {
    internal val cache: MutableMap<K, V> = mutableMapOf()

    operator fun get(key: K): Promise<V> {
        return cache[key]?.let { Promise.of(it) } ?: retrieve.invoke(key)
    }

    fun getOrPut(key: K, value: (K) -> V): V {
        return cache[key] ?: value.invoke(key).also { this.cache[key] = it }
    }

    fun collect(): List<V> = this.cache.values.toList()

}