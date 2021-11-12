package com.github.myraBot.diskord.gateway.listeners.impl

import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.UnavailableGuild
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadyEvent(
        @SerialName("v") val version: Int,
        @SerialName("user") val botUser: User,
        val guilds: List<UnavailableGuild>,
        @SerialName("session_id") val sessionId: String
) : Event() {

    init {
        Diskord.id = botUser.id
    }
}