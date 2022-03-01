package com.github.myraBot.diskord.gateway.events.impl.interactions

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.InteractionType
import com.github.myraBot.diskord.gateway.events.Event
import com.github.myraBot.diskord.gateway.events.impl.interactions.slashCommands.GuildSlashCommandEvent
import com.github.myraBot.diskord.gateway.events.impl.interactions.slashCommands.SlashCommandEvent
import com.github.myraBot.diskord.rest.builders.ComponentType

@kotlinx.serialization.Serializable
class InteractionCreateEvent(
    val interaction: Interaction,
) : Event() {

    override suspend fun prepareEvent() {
        when (interaction.type) {
            InteractionType.APPLICATION_COMMAND -> {
                if (!interaction.guildId.missing) GuildSlashCommandEvent(interaction)
                SlashCommandEvent(interaction)
            }
            InteractionType.MESSAGE_COMPONENT -> {
                when (interaction.interactionComponentData?.componentType) {
                    ComponentType.ACTION_ROW -> TODO()
                    ComponentType.BUTTON -> ButtonClickEvent(interaction)
                    ComponentType.SELECT_MENU -> SelectMenuEvent(interaction)
                    null -> TODO()
                }
            }
            InteractionType.PING -> TODO()
            InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE -> TODO()
        }
    }

}