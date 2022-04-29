package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionType
import bot.myra.diskord.common.entities.applicationCommands.components.items.ComponentType
import bot.myra.diskord.gateway.events.Event
import bot.myra.diskord.gateway.events.impl.interactions.slashCommands.GuildSlashCommandEvent
import bot.myra.diskord.gateway.events.impl.interactions.slashCommands.SlashCommandEvent

@kotlinx.serialization.Serializable
class InteractionCreateEvent(
    val interaction: Interaction,
) : Event() {

    override fun prepareEvent() = when (interaction.type) {
        InteractionType.PING                             -> TODO()
        InteractionType.APPLICATION_COMMAND              -> when (interaction.guildId.missing) {
            true  -> SlashCommandEvent(interaction)
            false -> GuildSlashCommandEvent(interaction)
        }
        InteractionType.MESSAGE_COMPONENT                -> when (interaction.interactionComponentData?.componentType) {
            ComponentType.ACTION_ROW  -> TODO()
            ComponentType.BUTTON      -> ButtonClickEvent(interaction)
            ComponentType.SELECT_MENU -> SelectMenuEvent(interaction)
            null                      -> TODO()
        }
        InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE -> AutoCompleteEvent(interaction)
    }.call()

}