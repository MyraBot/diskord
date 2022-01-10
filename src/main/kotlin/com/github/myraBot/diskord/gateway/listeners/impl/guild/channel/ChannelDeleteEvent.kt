package com.github.myraBot.diskord.gateway.listeners.impl.guild.channel

import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

@Serializable
data class ChannelDeleteEvent(
        val channelData: ChannelData
) : Event()