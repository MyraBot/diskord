package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.guild.Member

open class GuildSlashCommandEvent(
    override val interaction: Interaction
) : SlashCommandEvent(interaction) {
    override val member: Member get() = interaction.member!!
}