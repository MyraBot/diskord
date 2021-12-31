package diskord.gateway.listeners.impl.interactions

import diskord.common.entities.applicationCommands.Interaction
import diskord.common.entities.applicationCommands.InteractionType
import diskord.gateway.listeners.Event
import diskord.rest.builders.ComponentType

class InteractionCreateEvent(
        val data: Interaction
) : Event() {

    override suspend fun call() {
        when (data.type) {
            InteractionType.APPLICATION_COMMAND -> SlashCommandEvent(data).call()
            InteractionType.MESSAGE_COMPONENT -> {
                when (data.interactionComponentData?.componentType) {
                    ComponentType.ACTION_ROW -> TODO()
                    ComponentType.BUTTON -> ButtonClickEvent(data).call()
                    ComponentType.SELECT_MENU -> SelectMenuEvent(data).call()
                    null -> TODO()
                }
            }
            InteractionType.PING -> TODO()
            InteractionType.APPLICATION_COMMAND_AUTOCOMPLETE -> TODO()
        }

        super.call()
    }

}