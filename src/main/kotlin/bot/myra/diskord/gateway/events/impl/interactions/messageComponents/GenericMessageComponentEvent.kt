package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.InteractionData
import bot.myra.diskord.gateway.events.impl.interactions.GenericInteractionCreateEvent
import bot.myra.diskord.rest.behaviors.interaction.NonModalInteractionBehavior

abstract class GenericMessageComponentEvent(
    override val data: InteractionData,
    open val component: InteractionComponentData,
    override val diskord: Diskord
) : GenericInteractionCreateEvent(data, diskord), NonModalInteractionBehavior