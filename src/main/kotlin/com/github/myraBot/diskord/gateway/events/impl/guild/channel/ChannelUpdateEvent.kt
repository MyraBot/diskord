package com.github.myraBot.diskord.gateway.events.impl.guild.channel

import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
data class ChannelUpdateEvent(
        val channelData: ChannelData
) : Event()