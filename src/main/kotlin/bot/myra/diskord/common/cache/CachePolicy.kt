package bot.myra.diskord.common.cache

import bot.myra.diskord.common.cache.models.*

data class CachePolicy(
    var user: UserCachePolicy = UserCachePolicy(),
    var guild: GuildCachePolicy = GuildCachePolicy(),
    var member: MemberCachePolicy = MemberCachePolicy(),
    var voiceState: VoiceStateCachePolicy = VoiceStateCachePolicy(),
    var channel: ChannelCachePolicy = ChannelCachePolicy(),
    var message: MessageCachePolicy = MessageCachePolicy()
) {

    fun all(): List<GenericCachePolicy<*, *>> = listOf(
        user, guild, member, voiceState, channel
    )

    fun user(builder: UserCachePolicy.() -> Unit) {
        user = UserCachePolicy().apply(builder).apply { active = true }
        user.loadListeners()
    }

    fun guild(builder: GuildCachePolicy.() -> Unit) {
        guild = GuildCachePolicy().apply(builder).apply { active = true }
        guild.loadListeners()
    }

    fun member(builder: MemberCachePolicy.() -> Unit) {
        member = MemberCachePolicy().apply(builder).apply { active = true }
        member.loadListeners()
    }

    fun voiceState(builder: VoiceStateCachePolicy.() -> Unit) {
        voiceState = VoiceStateCachePolicy().apply(builder).apply { active = true }
        voiceState.loadListeners()
    }

    fun channel(builder: ChannelCachePolicy.() -> Unit) {
        channel = ChannelCachePolicy().apply(builder).apply { active = true }
        channel.loadListeners()
    }

    fun message(builder: MessageCachePolicy.() -> Unit) {
        message = MessageCachePolicy().apply(builder).apply { active = true }
        message.loadListeners()
    }

}

fun cachingPolicy(builder: CachePolicy.() -> Unit): CachePolicy = CachePolicy().apply(builder)