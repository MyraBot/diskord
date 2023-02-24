package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.gateway.events.impl.interactions.GenericInteractionCreateEvent
import bot.myra.diskord.rest.behaviors.interaction.NonModalInteractionBehavior

abstract class GenericMessageComponentEvent(
    override val interaction: Interaction,
    open val component: InteractionComponentData,
    override val diskord: Diskord
) : GenericInteractionCreateEvent(interaction, diskord), NonModalInteractionBehavior