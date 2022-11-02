package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.entities.guild.ExtendedGuild

class GuildCreateEvent(
    override val guild: ExtendedGuild
) : GenericGuildCreateEvent(guild)