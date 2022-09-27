package bot.myra.diskord.common.cache

/**
 * A cache key which holds optional information about the guild.
 *
 * @param T ID type of the actual object.
 * @property guild Optional guild id.
 * @property id The value of the ID.
 */
data class CacheKey<T>(val guild: String?, val id: T)

data class CacheGuildKey<T>(val guild: String, val id: T)


data class MemberCacheKey(val guild: String, val user: String)
data class VoiceStateCacheKey(val guild: String, val user: String)