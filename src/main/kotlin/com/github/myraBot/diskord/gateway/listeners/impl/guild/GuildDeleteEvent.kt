package com.github.myraBot.diskord.gateway.listeners.impl.guild

import com.github.myraBot.diskord.gateway.listeners.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-delete)
 *
 * @property id Guild id.
 */
data class GuildDeleteEvent(
        val id: String
) : Event()
