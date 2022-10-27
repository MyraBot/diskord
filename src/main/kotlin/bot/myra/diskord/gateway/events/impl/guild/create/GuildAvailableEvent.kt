package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.entities.guild.DetailedGuild

class GuildAvailableEvent(
    override val guild: DetailedGuild
) : GenericGuildCreateEvent(guild)