package bot.myra.diskord.gateway.events.impl.guild.delete

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.UnavailableGuild

// TODO handle guild unloading (make sure to handle reloading properly)

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-delete)
 *
 * @property guild The guild.
 */
data class GuildUnloadEvent(
    override val guild: UnavailableGuild,
    val cachedGuild: Guild?,
    override val diskord: Diskord
) : GenericGuildDeleteEvent(guild)
