package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.gateway.events.EventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

data class DoubleKey(val first: String, val second: String)

abstract class Cache<K, V> : EventListener {
    internal val cache: MutableMap<K, V> = mutableMapOf()

    fun get(key: K): Deferred<V?> {
        cache[key]?.let {
            return CompletableDeferred(it)
        } ?: run {
            return retrieveAsync(key)
        }
    }

    fun getOrPut(key: K, value: (K) -> V): V {
        return cache[key] ?: value.invoke(key).also { this.cache[key] = it }
    }

    fun collect(): List<V> = this.cache.values.toList()

    abstract fun retrieveAsync(key: K): Deferred<V?>

}