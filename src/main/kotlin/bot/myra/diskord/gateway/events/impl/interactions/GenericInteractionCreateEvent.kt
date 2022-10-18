package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.gateway.events.types.Event
import bot.myra.diskord.rest.behaviors.interaction.InteractionCreateBehavior
import kotlinx.serialization.Serializable

@Serializable
abstract class GenericInteractionCreateEvent(
    override val interaction: Interaction
) : Event(), InteractionCreateBehavior {
    val user: User get() = interaction.member?.user ?: interaction.user.value!!
    val userLocale: Locale? get() = interaction.locale.value
    val guildLocale: Locale? get() = interaction.guildLocale.value
}