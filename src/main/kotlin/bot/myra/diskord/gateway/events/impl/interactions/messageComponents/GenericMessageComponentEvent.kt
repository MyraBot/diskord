package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.components.items.ComponentType
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.events.impl.interactions.GenericInteractionCreateEvent
import kotlinx.serialization.json.decodeFromJsonElement

open class GenericMessageComponentEvent(
    override val interaction: Interaction
) : GenericInteractionCreateEvent(interaction) {
    val component: InteractionComponentData get() = interaction.data.value!!.let { JSON.decodeFromJsonElement(it) }

    override fun prepareEvent() = when (component.type) {
        ComponentType.ACTION_ROW  -> throw IllegalStateException()
        ComponentType.BUTTON      -> ButtonClickEvent(interaction)
        ComponentType.SELECT_MENU -> SelectMenuEvent(interaction)
    }.call()

}