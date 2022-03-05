package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.Event
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