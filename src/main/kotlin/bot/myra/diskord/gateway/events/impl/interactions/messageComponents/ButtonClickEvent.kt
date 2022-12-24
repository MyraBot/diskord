package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.components.Button
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.rest.EntityProvider
import kotlinx.coroutines.runBlocking

@Suppress("unused")
class ButtonClickEvent(
    override val interaction: Interaction,
    override val component: InteractionComponentData
) : GenericMessageComponentEvent(interaction, component) {
    val message: Message = interaction.message.value!!
    val guild: Guild? get() = runBlocking { EntityProvider.getGuild(interaction.guildId.value!!).value }
    val member: Member? get() = interaction.member
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == component.customId }
            .let { return it.asButton() }
}