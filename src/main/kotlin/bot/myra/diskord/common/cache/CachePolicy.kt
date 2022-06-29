package bot.myra.diskord.common.cache

import bot.myra.diskord.common.cache.models.*

data class CachePolicy(
    var user: UserCachePolicy = DisabledUserCachePolicy(),
    var guild: GuildCachePolicy = DisabledGuildCachePolicy(),
    var member: MemberCachePolicy = DisabledMemberCachePolicy(),
    var voiceState: VoiceStateCachePolicy = DisabledVoiceStateCachePolicy(),
    var channel: ChannelCachePolicy = DisabledChannelCachePolicy(),
    var message: MessageCachePolicy = DisabledMessageCachePolicy()
) {

    fun all(): List<GenericCachePolicy<*, *>> = listOf(
        user, guild, member, voiceState, channel
    )

    fun user(builder: UserCachePolicy.() -> Unit) {
        user = MutableUserCachePolicy().apply(builder)
    }

    fun guild(builder: MutableGuildCachePolicy.() -> Unit) {
        guild = MutableGuildCachePolicy().apply(builder)
    }

    fun member(builder: MemberCachePolicy.() -> Unit) {
        member = MutableMemberCachePolicy().apply(builder)
    }

    fun voiceState(builder: VoiceStateCachePolicy.() -> Unit) {
        voiceState = MutableVoiceStateCachePolicy().apply(builder)
    }

    fun channel(builder: ChannelCachePolicy.() -> Unit) {
        channel = MutableChannelCachePolicy().apply(builder)
    }

    fun message(builder: MessageCachePolicy.() -> Unit) {
        message = MutableMessageCachePolicy().apply(builder)
    }

}

fun cachingPolicy(builder: CachePolicy.() -> Unit): CachePolicy = CachePolicy().apply(builder)