package bot.myra.diskord.common.cache

import bot.myra.diskord.common.cache.models.*

data class CachePolicy(
    var userCache: UserCachePolicy = UserCachePolicy(),
    var guildCache: GuildCachePolicy = GuildCachePolicy(),
    var memberCache: MemberCachePolicy = MemberCachePolicy(),
    var voiceStateCache: VoiceStateCachePolicy = VoiceStateCachePolicy(),
    var channelCache: ChannelCachePolicy = ChannelCachePolicy()
) {

    fun all(): List<GenericCachePolicy<*, *>> = listOf(
        userCache, guildCache, memberCache, voiceStateCache, channelCache
    )

    fun user(builder: UserCachePolicy.() -> Unit) {
        userCache = UserCachePolicy().apply(builder)
    }

    fun guild(builder: GuildCachePolicy.() -> Unit) {
        guildCache = GuildCachePolicy().apply(builder)
    }

    fun member(builder: MemberCachePolicy.() -> Unit) {
        memberCache = MemberCachePolicy().apply(builder)
    }

    fun voiceState(builder: VoiceStateCachePolicy.() -> Unit) {
        voiceStateCache = VoiceStateCachePolicy().apply(builder)
    }

    fun channel(builder: ChannelCachePolicy.() -> Unit) {
        channelCache = ChannelCachePolicy().apply(builder)
    }

}

fun cachingPolicy(builder: CachePolicy.() -> Unit): CachePolicy = CachePolicy().apply(builder)