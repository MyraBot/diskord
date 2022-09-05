package bot.myra.diskord.common.cache

import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.gateway.events.loadListeners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ForkJoinPool

private val coroutinesScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

data class DoubleKey<F, S>(val first: F, val second: S)

abstract class GenericCachePolicy<K, V>(
    internal var view: ViewCache<V>? = null,
    internal var get: GetCache<K, V>? = null,
    internal var update: UpdateCache<V>? = null,
    internal var remove: RemoveCache<K>? = null
) : EventListener {
     val mutex = Mutex()

    init {
        loadListeners()
    }

    fun view(action: ViewCache<V>) = run { view = action }
    fun get(action: GetCache<K, V>) = run { get = action }
    fun update(action: UpdateCache<V>) = run { update = action }
    fun remove(action: RemoveCache<K>) = run { remove = action }

    suspend fun view(): List<V> = mutex.withLock { view?.invoke() ?: emptyList() }
    suspend fun get(key: K) = mutex.withLock { get?.invoke(key) }
    suspend fun update(value: V) = mutex.withLock { update?.invoke(value) }
    suspend fun remove(key: K) = mutex.withLock { remove?.invoke(key) }
}