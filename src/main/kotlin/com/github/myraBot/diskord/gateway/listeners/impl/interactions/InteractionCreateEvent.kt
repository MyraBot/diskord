package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionType
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.builders.ComponentType

class InteractionCreateEvent(
        val data: Interaction
) : Event() {

    override suspend fun call() {
        when (data.type) {
            InteractionType.APPLICATION_COMMAND -> SlashCommandEvent(data).call()
            InteractionType.MESSAGE_COMPONENT -> {
                when (data.interactionComponentData?.componentType) {
                    ComponentType.BUTTON -> ButtonClickEvent(data).call()
                    ComponentType.ACTION_ROW -> TODO()
                    ComponentType.SELECT_MENU -> TODO()
                    null -> TODO()
                }
            }
            InteractionType.PING -> TODO()
            InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE -> TODO()
        }

        super.call()
    }

}