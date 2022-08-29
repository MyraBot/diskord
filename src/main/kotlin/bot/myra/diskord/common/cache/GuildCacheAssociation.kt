package bot.myra.diskord.common.cache

import bot.myra.diskord.rest.behaviors.Entity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GuildCacheAssociation<T : Entity> {
    private val mutex = Mutex()

    private var update: UpdateCache<T>? = null
    private var remove: RemoveCache<DoubleKey<String, String>>? = null
    private var viewByGuild: ViewByGuildId<String, T>? = null
    private var associatedByGuild: AssociatedByGuildId<String>? = null

    suspend fun view(id: String): List<T>? = mutex.withLock { viewByGuild?.invoke(id) }
    suspend fun associatedByGuild(guild: String) = mutex.withLock { associatedByGuild?.invoke(guild) }
    suspend fun update(item: T) = mutex.withLock { update?.invoke(item) }
    suspend fun remove(guild: String, id: String) = mutex.withLock { remove?.invoke(DoubleKey(guild, id)) }

    fun update(action: UpdateCache<T>) = run { update = action }
    fun remove(action: RemoveCache<DoubleKey<String, String>>) = run { remove = action }
    fun viewOrNull(action: ViewByGuildId<String, T>) = run { viewByGuild = action }
    fun associatedByGuild(action: AssociatedByGuildId<String>?) = run { associatedByGuild = action }
}