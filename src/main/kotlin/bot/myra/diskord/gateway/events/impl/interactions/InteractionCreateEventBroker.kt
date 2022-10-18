package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionType
import bot.myra.diskord.gateway.events.impl.interactions.messageComponents.MessageComponentEventBroker
import bot.myra.diskord.gateway.events.impl.interactions.slashCommands.SlashCommandEventBroker
import bot.myra.diskord.gateway.events.types.EventBroker

class InteractionCreateEventBroker(
    val interaction: Interaction,
) : EventBroker() {

    override suspend fun choose() = when (interaction.type) {
        InteractionType.PING                             -> TODO()
        InteractionType.APPLICATION_COMMAND              -> SlashCommandEventBroker(interaction)
        InteractionType.MESSAGE_COMPONENT                -> MessageComponentEventBroker(interaction)
        InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE -> AutoCompleteEvent(interaction)
    }

}