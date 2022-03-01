package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionType
import bot.myra.diskord.gateway.events.Event
import bot.myra.diskord.gateway.events.impl.interactions.slashCommands.GuildSlashCommandEvent
import bot.myra.diskord.gateway.events.impl.interactions.slashCommands.SlashCommandEvent
import bot.myra.diskord.rest.builders.ComponentType

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