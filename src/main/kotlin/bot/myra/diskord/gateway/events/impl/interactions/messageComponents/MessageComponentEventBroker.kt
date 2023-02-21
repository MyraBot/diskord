package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.components.ComponentType
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.events.types.EventBroker
import kotlinx.serialization.json.decodeFromJsonElement

class MessageComponentEventBroker(
    val interaction: Interaction,
    override val diskord: Diskord
) : EventBroker() {
    val component: InteractionComponentData = interaction.interactionData.value!!.let { JSON.decodeFromJsonElement(it) }

    override suspend fun choose() = when (component.type) {
        ComponentType.ACTION_ROW  -> throw IllegalStateException()
        ComponentType.BUTTON      -> ButtonClickEvent(interaction, component, diskord)
        ComponentType.SELECT_MENU -> SelectMenuEvent(interaction, component, diskord)
    }

}