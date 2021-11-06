package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.rest.behaviors.channel.ChannelBehavior
import kotlinx.serialization.Serializable

@Serializable
data class MessageChannel(
        override val id: String
) : ChannelBehavior