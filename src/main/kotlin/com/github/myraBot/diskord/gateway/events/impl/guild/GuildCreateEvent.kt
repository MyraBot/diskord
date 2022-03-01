package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Can be fired in three different scenarios:
 * 1. To backfill information for unavailable guilds sent in the [com.github.myraBot.diskord.gateway.events.impl.ReadyEvent].
 * 2. When a guild becomes available again to the client.
 * 3. When the bot joins a new Guild.
 *
 * @property guild The guild.
 */
@Serializable
data class GuildCreateEvent(
    val guild: Guild
) : Event()