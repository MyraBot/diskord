package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.entityData.channel.ChannelData
import com.github.myraBot.diskord.rest.behaviors.channel.impl.TextChannelBehavior

data class TextChannel(
        val data: ChannelData
) : TextChannelBehavior {
    override val id: String = data.id
}