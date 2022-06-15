package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.guild.Member

/**
 * Slash command event which runs when a slash command got invoked
 * in a guild channel.
 *
 * @property interaction
 */
open class GuildSlashCommandEvent(
    override val interaction: Interaction
) : GenericSlashCommandEvent(interaction) {
    override val member: Member get() = interaction.member!!
}