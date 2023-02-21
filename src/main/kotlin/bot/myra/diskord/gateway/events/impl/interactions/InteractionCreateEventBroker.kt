package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionData
import bot.myra.diskord.common.entities.applicationCommands.InteractionType
import bot.myra.diskord.gateway.events.impl.interactions.messageComponents.MessageComponentEventBroker
import bot.myra.diskord.gateway.events.impl.interactions.slashCommands.SlashCommandEventBroker
import bot.myra.diskord.gateway.events.types.EventAction
import bot.myra.diskord.gateway.events.types.EventBroker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

class InteractionCreateEventBroker(
    val interaction: Interaction,
    override val diskord: Diskord,
) : EventBroker() {

    companion object {
        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): InteractionCreateEventBroker {
            val data = decoder.decodeFromJsonElement<InteractionData>(json)
            val interaction = Interaction(diskord, data)
            return InteractionCreateEventBroker(interaction, diskord)
        }
    }

    override suspend fun choose(): EventAction = when (interaction.type) {
        InteractionType.PING                             -> TODO()
        InteractionType.APPLICATION_COMMAND              -> SlashCommandEventBroker(interaction, diskord)
        InteractionType.MESSAGE_COMPONENT                -> MessageComponentEventBroker(interaction, diskord)
        InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE -> AutoCompleteEvent(interaction, diskord)
    }

}