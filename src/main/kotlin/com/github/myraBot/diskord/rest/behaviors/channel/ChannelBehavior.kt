package com.github.myraBot.diskord.rest.behaviors.channel

import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.utilities.Mention

interface ChannelBehavior : Entity {
    val mention get() = Mention.channel(id)
}