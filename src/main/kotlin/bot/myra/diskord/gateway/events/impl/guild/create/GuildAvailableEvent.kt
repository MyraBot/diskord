package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.entities.guild.ExtendedGuild

class GuildAvailableEvent(
    override val guild: ExtendedGuild
) : GenericGuildCreateEvent(guild)