package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.gateway.events.types.Event
import bot.myra.diskord.rest.behaviors.interaction.InteractionCreateBehavior

abstract class GenericInteractionCreateEvent(
    open val interaction: Interaction,
    override val diskord: Diskord
) : Event(), InteractionCreateBehavior {
    override val data get() = interaction.data

    val id get() = data.id
    val applicationId get() = data.applicationId
    val type get() = data.type
    val interactionData get() = data.data
    val guildId get() = data.guildId
    val channelId get() = data.channelId
    val user get() = User(data.memberData.value?.user ?: data.user.value!!, diskord)
    open val member get() = interaction.member
    val token get() = data.token
    val version get() = data.version
    val message get() = Message(interaction.message.value!!, diskord)
    val locale get() = data.locale
    val guildLocale get() = data.guildLocale
}