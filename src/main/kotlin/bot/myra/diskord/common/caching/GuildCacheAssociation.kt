package bot.myra.diskord.common.caching

import bot.myra.diskord.rest.behaviors.Entity

class GuildCacheAssociation<T : Entity> {
    private var update: UpdateCache<T>? = null
    private var remove: RemoveCache<DoubleKey<String, String>>? = null
    private var viewByGuild: ViewByGuildId<String, T>? = null
    private var associatedByGuild: AssociatedByGuildId<String>? = null

    fun view(id: String): List<T>? = viewByGuild?.invoke(id)
    fun associatedByGuild(guild: String) = associatedByGuild?.invoke(guild)
    fun update(item: T) = update?.invoke(item)
    fun remove(guild: String, id: String) = remove?.invoke(DoubleKey(guild, id))

    fun update(action: UpdateCache<T>) = run { update = action }
    fun remove(action: RemoveCache<DoubleKey<String, String>>) = run { remove = action }
    fun viewOrNull(action: ViewByGuildId<String, T>) = run { viewByGuild = action }
    fun associatedByGuild(action: AssociatedByGuildId<String>?) = run { associatedByGuild = action }
}