package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.events.impl.interactions.NonModalInteractionEvent
import kotlinx.serialization.json.decodeFromJsonElement

abstract class GenericMessageComponentEvent(
    override val interaction: Interaction
) : NonModalInteractionEvent(interaction) {
    val component: InteractionComponentData get() = interaction.data.value!!.let { JSON.decodeFromJsonElement(it) }
}