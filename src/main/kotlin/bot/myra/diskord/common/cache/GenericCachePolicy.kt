package bot.myra.diskord.common.cache

import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.rest.request.Result
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class GenericCachePolicy<K, V>(
    internal var view: ViewCache<V>? = null,
    internal var get: GetCache<K, V>? = null,
    internal var update: UpdateCache<V>? = null,
    internal var remove: RemoveCache<CacheKey<K>>? = null
) : EventListener {
    val mutex = Mutex()

    /**
     * Event functions required for the cache to work.
     * They are stored separately from normal events which
     * are stored in [bot.myra.diskord.common.Diskord.listeners].
     */
    internal val eventFunctions = findEventFunction()

    fun view(action: ViewCache<V>) = run { view = action }
    fun get(action: GetCache<K, V>) = run { get = action }
    fun update(action: UpdateCache<V>) = run { update = action }
    fun remove(action: RemoveCache<CacheKey<K>>) = run { remove = action }

    open suspend fun view(): List<V> = mutex.withLock { view?.invoke() ?: emptyList() }
    open suspend fun get(key: K): Result<V> = mutex.withLock {
        val value = get?.invoke(key)
        if (value == null) Result(null, HttpStatusCode.NotFound, null)
        else Result(value, HttpStatusCode.OK, null)
    }

    open suspend fun update(value: V) = mutex.withLock { update?.invoke(value) }
    open suspend fun remove(key: K, guild: String? = null) = mutex.withLock { remove?.invoke(CacheKey(guild, key)) }

    abstract fun getAsKey(value: V): K
}