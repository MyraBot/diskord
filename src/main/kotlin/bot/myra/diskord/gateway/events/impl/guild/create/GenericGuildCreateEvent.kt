package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Can be fired in three different scenarios:
 * 1. To backfill information for unavailable guilds sent in the [bot.myra.diskord.gateway.events.impl.ReadyEvent].
 * 2. When a guild becomes available again to the client.
 * 3. When the bot joins a new Guild.
 *
 * @property guild The guild.
 */
@Serializable
abstract class GenericGuildCreateEvent(
    @Contextual
    open val guild: Guild
) : Event()