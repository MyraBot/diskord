package bot.myra.diskord.gateway.events.impl.guild.delete

import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.types.Event

/**
 * Sent when a guild becomes or was already unavailable due to an outage,
 * or when the user leaves or is removed from a guild.
 */
@Suppress("unused")
abstract class GenericGuildDeleteEvent(
    open val guild: UnavailableGuild
) : Event() {
    //val cachedGuild: Guild? = runBlocking { diskord.getGuild(guild.id).value }
}
