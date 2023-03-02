package bot.myra.diskord.gateway.events.impl.interactions.messageComponents

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.InteractionComponentData
import bot.myra.diskord.common.entities.applicationCommands.components.Button
import bot.myra.diskord.common.entities.guild.Guild
import kotlinx.coroutines.runBlocking

@Suppress("unused")
class ButtonClickEvent(
    override val interaction: Interaction,
    override val component: InteractionComponentData,
    override val diskord: Diskord,
) : GenericMessageComponentEvent(interaction, component, diskord) {
    override val modifier = interaction.modifier
    override val followupModifier = interaction.followupModifier

    val guild: Guild? get() = runBlocking { diskord.getGuild(interaction.guildId.value!!).value }
    val button: Button
        get() = message.components
            .asSequence()
            .flatMap { it.components }
            .first { it.id == component.customId }
            .let { return it.asButton() }
}