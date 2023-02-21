package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.components.Button
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import kotlinx.coroutines.runBlocking

@Suppress("unused")
class ButtonClickEvent(
    val interaction: Interaction,
    override val component: InteractionComponentData,
    override val diskord: Diskord,
) : GenericMessageComponentEvent(interaction.data, component, diskord) {
    val message: Message = Message(interaction.message.value!!, diskord)
    val guild: Guild? get() = runBlocking { diskord.getGuild(interaction.guildId.value!!).value }
    val member: Member? get() = interaction.member
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == component.customId }
            .let { return it.asButton() }
}