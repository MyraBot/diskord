package com.github.myraBot.diskord.gateway

import com.github.myraBot.diskord.common.*
import com.github.myraBot.diskord.gateway.listeners.EventListener

enum class Cache(val cache: EventListener, val intents: MutableSet<GatewayIntent> = mutableSetOf()) {

    GUILD(guildCache),
    MEMBER(memberCache, mutableSetOf(GatewayIntent.GUILD_MEMBERS)),
    CHANNEL(channelCache),
    ROLE(roleCache),
    VOICE_STATE(voiceCache, mutableSetOf(GatewayIntent.GUILD_VOICE_STATES))

}