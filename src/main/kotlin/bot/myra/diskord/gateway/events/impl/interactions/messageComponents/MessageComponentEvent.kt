package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.components.ComponentType

open class MessageComponentEvent(
    override val interaction: Interaction
) : GenericMessageComponentEvent(interaction) {

    override suspend fun handle() = when (component.type) {
        ComponentType.ACTION_ROW  -> throw IllegalStateException()
        ComponentType.BUTTON      -> ButtonClickEvent(interaction)
        ComponentType.SELECT_MENU -> SelectMenuEvent(interaction)
    }.call()

}