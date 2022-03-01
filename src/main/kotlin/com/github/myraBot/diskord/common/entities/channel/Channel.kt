package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.common.utilities.Mention

interface Channel : Entity {
    val data: ChannelData

    override val id: String get() = data.id
    val source: ChannelType get() = data.source

    val mention: String get() = Mention.channel(id)
}