package bot.myra.diskord.gateway.handler.intents

import bot.myra.diskord.common.caching.*
import bot.myra.diskord.gateway.events.EventListener

@Suppress("unused")
enum class Cache(
    val listener: EventListener,
    val intents: MutableSet<GatewayIntent> = mutableSetOf()
) {

    GUILD(GuildCache),
    MEMBER(MemberCache, mutableSetOf(GatewayIntent.GUILD_MEMBERS)),
    CHANNEL(ChannelCache),
    ROLE(RoleCache),
    VOICE_STATE(VoiceCache, mutableSetOf(GatewayIntent.GUILD_VOICE_STATES))

}