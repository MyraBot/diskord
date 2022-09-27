package bot.myra.diskord.common.cache

import kotlinx.coroutines.sync.withLock

abstract class GenericGuildCachePolicy<K, V>(
    private var guildUpdate: UpdateCache<GuildSafeValue<V>>? = null,
    private var guildRemove: RemoveCache<CacheGuildKey<K>>? = null,
    private var guildView: ((String) -> List<V>)? = null,
) : GenericCachePolicy<K, V>() {

    /**
     * Wrapper for the value with a guarantee of a non-nullable guild ID.
     * Used in [update].
     *
     * @param V The data type of the value.
     * @property guild The guild id of the value.
     * @property value The updated value.
     */
    data class GuildSafeValue<V>(val guild: String, val value: V)

    fun guildUpdate(action: UpdateCache<GuildSafeValue<V>>) = run { guildUpdate = action }
    fun guildRemove(action: RemoveCache<CacheGuildKey<K>>) = run { guildRemove = action }
    fun guildView(action: (String) -> List<V>) = run { guildView = action }

    abstract fun isFromGuild(value: V): String?

    override suspend fun view(): List<V> {
        return super.view()
    }

    override suspend fun get(key: K): V? {
        return super.get(key)
    }

    override suspend fun update(value: V) {
        isFromGuild(value)?.apply {
            mutex.withLock { guildUpdate?.invoke(GuildSafeValue(this, value)) }
        }
        super.update(value)
    }

    override suspend fun remove(key: K, guild: String?) {
        super.remove(key, guild)
        guild?.apply {
            mutex.withLock { guildRemove?.invoke(CacheGuildKey(this, key)) }
        }
    }

    suspend fun viewByGuild(guild: String): List<V>? = mutex.withLock { guildView?.invoke(guild) }

    suspend fun getGuild(id: K): String? = get(id)?.let { value -> isFromGuild(value) }

}