package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionType
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.builders.ComponentType

class InteractionCreateEvent(
        val data: Interaction
) : Event() {

    override suspend fun call() {
        println("DISPATCHED INTERACTION CREATE EVENT")
        when (data.type) {
            InteractionType.APPLICATION_COMMAND -> SlashCommandEvent(data).call().also { println("DISAPTCHED A SLASH COMMAND EVENT") }
            InteractionType.MESSAGE_COMPONENT -> {
                println("INTERACTION IS A MESSAGE COMPONENT")
                println(data.interactionComponentData?.componentType)
                when (data.interactionComponentData?.componentType) {
                    ComponentType.BUTTON -> println("EXECUTING A BUTTON EVENT!").also { ButtonClickEvent(data).call() }
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