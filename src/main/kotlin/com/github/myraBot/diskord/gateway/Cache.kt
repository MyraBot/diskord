package com.github.myraBot.diskord.gateway

import com.github.myraBot.diskord.common.caching.*
import com.github.myraBot.diskord.gateway.listeners.EventListener

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