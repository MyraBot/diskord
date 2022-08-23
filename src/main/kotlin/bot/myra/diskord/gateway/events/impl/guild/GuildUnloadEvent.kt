package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-delete)
 *
 * @property guild The guild.
 */
data class GuildUnloadEvent(
    val guild: UnavailableGuild,
    var cachedGuild: Guild?
) : Event()
