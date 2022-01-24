package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior
import kotlinx.serialization.Serializable

@Serializable
abstract class InteractionCreateEvent(
        open val data: Interaction,
) : Event(), InteractionCreateBehavior {
    override val interaction: Interaction get() = data
}