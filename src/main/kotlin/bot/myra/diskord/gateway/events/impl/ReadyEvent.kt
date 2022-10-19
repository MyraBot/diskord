package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadyEvent(
    @SerialName("v") val version: Int,
    @SerialName("user") val botUser: User,
    val guilds: List<UnavailableGuild>,
    @SerialName("session_id") val sessionId: String,
    @SerialName("resume_gateway_url") val resumeGatewayUrl: String
) : Event() {

    override suspend fun handle() {
        Diskord.apply {
            gateway.session = sessionId
            gateway.resumeUrl = resumeGatewayUrl
            id = botUser.id
            guildIds.addAll(guilds.map(UnavailableGuild::id))
            unavailableGuilds.addAll(guilds.map(UnavailableGuild::id))
        }

        if (Diskord.initialConnection) {
            InitialReadyEvent(this@ReadyEvent).handle()
            Diskord.initialConnection = false
        }
        call()
    }

}