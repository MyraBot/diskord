package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.components.items.button.Button
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.rest.EntityProvider
import kotlinx.coroutines.runBlocking

data class ButtonClickEvent(
    override val data: Interaction,
) : GenericInteractionCreateEvent(data) {
    val message: Message = data.message.value!!
    val guild: Guild? get() =  runBlocking { EntityProvider.getGuild(data.guildId.value!!) }
    val member: Member? get() = data.member
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == data.interactionComponentData?.customId }
            .let { return it.asButton() }
}