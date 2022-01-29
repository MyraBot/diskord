package com.github.myraBot.diskord.rest.builders

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class InteractionMessageBuilder(
    val interaction: Interaction
) : MessageBuilder()