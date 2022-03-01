package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.gateway.events.Event
import bot.myra.diskord.rest.behaviors.InteractionCreateBehavior
import kotlinx.serialization.Serializable

@Serializable
abstract class GenericInteractionCreateEvent(
    open val data: Interaction,
) : Event(), InteractionCreateBehavior {
    override val interaction: Interaction get() = data

    val user: User get() = data.member?.user ?: data.user.value!!
}