package bot.myra.diskord.gateway.events.impl.guild.delete

import bot.myra.diskord.common.entities.guild.UnavailableGuild

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-delete)
 *
 * @property guild The guild.
 */
data class GuildUnloadEvent(
    override val guild: UnavailableGuild
) : GenericGuildDeleteEvent(guild)
