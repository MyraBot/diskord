package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.guild.Member

/**
 * Slash command event which runs when a slash command got invoked
 * in a guild channel.
 */
open class GuildSlashCommandEvent(
    override val interaction: Interaction,
    override val diskord: Diskord
) : GenericSlashCommandEvent(interaction, diskord) {
    override val member: Member get() = interaction.member!!
}