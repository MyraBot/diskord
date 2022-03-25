package bot.myra.diskord.common.caching

import bot.myra.diskord.common.caching.models.*

data class CachePolicy(
    var userCachePolicy: UserCachePolicy = UserCachePolicy(),
    var guildCachePolicy: GuildCachePolicy = GuildCachePolicy(),
    var memberCachePolicy: MemberCachePolicy = MemberCachePolicy(),
    var voiceStateCachePolicy: VoiceStateCachePolicy = VoiceStateCachePolicy(),
    var channelCachePolicy: ChannelCachePolicy = ChannelCachePolicy(),
    var roleCachePolicy: RoleCachePolicy = RoleCachePolicy()
) {

    fun all(): List<GenericCachePolicy<*, *>> = listOf(
        userCachePolicy, guildCachePolicy, memberCachePolicy, voiceStateCachePolicy, channelCachePolicy, roleCachePolicy
    )

    fun user(builder: UserCachePolicy.() -> Unit) {
        userCachePolicy = UserCachePolicy().apply(builder)
    }

    fun guild(builder: GuildCachePolicy.() -> Unit) {
        guildCachePolicy = GuildCachePolicy().apply(builder)
    }

    fun member(builder: MemberCachePolicy.() -> Unit) {
        memberCachePolicy = MemberCachePolicy().apply(builder)
    }

    fun voiceState(builder: VoiceStateCachePolicy.() -> Unit) {
        voiceStateCachePolicy = VoiceStateCachePolicy().apply(builder)
    }

    fun channel(builder: ChannelCachePolicy.() -> Unit) {
        channelCachePolicy = ChannelCachePolicy().apply(builder)
    }

    fun role(builder: RoleCachePolicy.() -> Unit) {
        roleCachePolicy = RoleCachePolicy().apply(builder)
    }

}

fun cachingPolicy(builder: CachePolicy.() -> Unit): CachePolicy = CachePolicy().apply(builder)