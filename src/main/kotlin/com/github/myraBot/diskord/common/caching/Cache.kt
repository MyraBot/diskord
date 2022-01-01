package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.gateway.listeners.EventListener

abstract class Cache<K, V> : EventListener() {
    internal val map: MutableMap<K, V> = mutableMapOf()
    val size: Int get() = map.size

    operator fun get(key: K): V? = map.getOrElse(key) { retrieve(key)?.also { map[key] = it } }
    abstract fun retrieve(key: K): V?
    abstract fun update(value: V)
}