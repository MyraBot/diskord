package bot.myra.diskord.gateway.events.impl.guild.delete

import bot.myra.diskord.common.entities.guild.UnavailableGuild

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-delete)
 *
 * @property guild The guild.
 */
data class GuildLeaveEvent(
    override val guild: UnavailableGuild,
) : GenericGuildDeleteEvent(guild)
