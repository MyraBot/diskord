package com.github.myraBot.diskord.gateway.listeners.impl.guild.channel

import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

@Serializable
data class ChannelUpdateEvent(
        val channel: Channel
) : Event()