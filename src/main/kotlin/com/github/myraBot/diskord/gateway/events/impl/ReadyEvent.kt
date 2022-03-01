package com.github.myraBot.diskord.gateway.events.impl

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.UnavailableGuild
import com.github.myraBot.diskord.gateway.events.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadyEvent(
    @SerialName("v") val version: Int,
    @SerialName("user") val botUser: User,
    val guilds: List<UnavailableGuild>,
    @SerialName("session_id") val sessionId: String,
) : Event() {

    override suspend fun prepareEvent() {
        Diskord.apply {
            websocket.session = sessionId
            id = botUser.id
            guildIds.addAll(guilds.map(UnavailableGuild::id))
            unavailableGuilds.addAll(guilds.map(UnavailableGuild::id))
        }
    }

}