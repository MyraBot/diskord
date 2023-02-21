package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.PossibleUnavailableGuild

data class GuildCreateEvent(
    override val guild: PossibleUnavailableGuild,
    override val diskord: Diskord
) : GenericGuildCreateEvent(guild)