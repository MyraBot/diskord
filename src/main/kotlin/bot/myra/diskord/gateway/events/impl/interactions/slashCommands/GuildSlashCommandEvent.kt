package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.guild.Member

open class GuildSlashCommandEvent(data: Interaction) : SlashCommandEvent(data) {
    override val member: Member get() = data.member!!
}