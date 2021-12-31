package diskord.gateway.listeners.impl

import diskord.Diskord
import diskord.common.entities.User
import diskord.common.entities.guild.UnavailableGuild
import diskord.gateway.listeners.Event
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