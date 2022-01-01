package com.github.myraBot.diskord.gateway.listeners.impl.guild

import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

/**
 * Can be fired in three different scenarios:
 * 1. To backfill information for unavailable guilds sent in the [com.github.myraBot.diskord.gateway.listeners.impl.ReadyEvent].
 * 2. When a guild becomes available again to the client.
 * 3. When the bot joins a new Guild.
 *
 * @property guild The guild.
 */
@Serializable
data class GuildCreateEvent(
        val guild: Guild
) : Event()