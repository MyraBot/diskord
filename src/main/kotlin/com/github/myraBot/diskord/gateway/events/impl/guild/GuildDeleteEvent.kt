package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.gateway.events.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-delete)
 *
 * @property id Guild id.
 */
data class GuildDeleteEvent(
        val id: String
) : Event()
