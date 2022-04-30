package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.components.items.button.Button
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.rest.EntityProvider
import kotlinx.coroutines.runBlocking

data class ButtonClickEvent(
    override val interaction: Interaction
) : GenericMessageComponentEvent(interaction) {
    val message: Message = interaction.message.value!!
    val guild: Guild? get() = runBlocking { EntityProvider.getGuild(interaction.guildId.value!!) }
    val member: Member? get() = interaction.member
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == component.customId }
            .let { return it.asButton() }
}