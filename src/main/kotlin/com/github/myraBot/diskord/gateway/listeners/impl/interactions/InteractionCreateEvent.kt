package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.interaction.Interaction
import com.github.myraBot.diskord.common.entities.interaction.InteractionType
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.builders.ComponentType

class InteractionCreateEvent(
        val data: Interaction
) : Event() {

    override suspend fun call() {
        if (data.type == InteractionType.MESSAGE_COMPONENT) {
            val type = data.interactionData?.componentType
            when {
                type == ComponentType.BUTTON -> ButtonClickEvent(data).call()
            }
        }

        super.call()
    }

}