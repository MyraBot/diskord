package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.entities.guild.PossibleUnavailableGuild
import bot.myra.diskord.gateway.events.types.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway-events#guild-create)
 *
 * This event can be sent in three different scenarios:
 *
 * - When a user is initially connecting, to lazily load and backfill information for all unavailable guilds sent in the Ready event. Guilds that are unavailable due to an outage will send a Guild Delete event.
 * - When a Guild becomes available again to the client.
 * - When the current user joins a new Guild.
 *
 * During an outage, the guild object in scenarios 1 and 3 may be marked as unavailable.
 *
 * @property guild The guild.
 */
abstract class GenericGuildCreateEvent(
    open val guild: PossibleUnavailableGuild
) : Event() {
    override suspend fun handle() {
        diskord.unavailableGuilds.remove(guild.id)?.also {
            guild.asExtendedGuild()?.asGuild()?.let { fullGuild ->
                it.complete(fullGuild)
            }
        }
    }
}