package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.entities.guild.Guild

class GuildCreateEvent(
    override val guild: Guild
) : GenericGuildCreateEvent(guild)