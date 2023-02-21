package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.applicationCommands.InteractionData
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.gateway.events.types.Event
import bot.myra.diskord.rest.behaviors.interaction.InteractionCreateBehavior

abstract class GenericInteractionCreateEvent(
    override val data: InteractionData,
    override val diskord: Diskord
) : Event(), InteractionCreateBehavior {
    val user: User get() = User(data.memberData.value?.user ?: data.user.value!!, diskord)
    val userLocale: Locale? get() = data.locale.value
    val guildLocale: Locale? get() = data.guildLocale.value
}