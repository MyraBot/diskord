package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

/**
 * Can be fired in three different scenarios:
 * 1. To backfill information for unavailable guilds sent in the [com.github.myraBot.diskord.gateway.events.impl.ReadyEvent].
 * 2. When a guild becomes available again to the client.
 * 3. When the bot joins a new Guild.
 *
 * @property guild The guild.
 */
@Serializable
data class GuildLoadEvent(
    val guild: Guild
) : Event() {

    override suspend fun prepareEvent() {
        // Guild was unavailable
        if (Diskord.unavailableGuilds.contains(guild.id)) {
            Diskord.unavailableGuilds.remove(guild.id)
            GuildAvailableEvent(guild)
        }
        // Bot joined guild
        else GuildCreateEvent(guild)
    }

}