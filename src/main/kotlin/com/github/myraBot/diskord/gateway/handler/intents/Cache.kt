package com.github.myraBot.diskord.gateway.handler.intents

import com.github.myraBot.diskord.common.caching.*
import com.github.myraBot.diskord.gateway.events.EventListener

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