package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.rest.behaviors.channel.impl.TextChannelBehavior
import kotlinx.serialization.Serializable

@Serializable
data class MessageChannel(
        override val id: String
) : TextChannelBehavior