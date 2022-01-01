package com.github.myraBot.diskord.gateway.listeners.impl.guild

import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

@Serializable
data class GuildCreateEvent(
        val guild: Guild
) : Event()