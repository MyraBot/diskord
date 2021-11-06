package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.rest.behaviors.channel.impl.TextChannelBehavior

data class TextChannel(
        val data: Channel
) : TextChannelBehavior {
    override val id: String = data.id
}