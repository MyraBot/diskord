package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.common.entities.user.UserData
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Suppress("unused")
class ReadyEvent(
    val version: Int,
    private val botUserData: UserData,
    val guilds: List<UnavailableGuild>,
    val sessionId: String,
    val resumeGatewayUrl: String,
    override val diskord: Diskord
) : Event() {
    val botUser get() = User(botUserData, diskord)

    companion object {
        @Serializable
        private data class Data(
            @SerialName("v") val version: Int,
            @SerialName("user") val botUser: UserData,
            val guilds: List<UnavailableGuild>,
            @SerialName("session_id") val sessionId: String,
            @SerialName("resume_gateway_url") val resumeGatewayUrl: String
        )

        fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): ReadyEvent {
            val event = decoder.decodeFromJsonElement<Data>(json)
            return ReadyEvent(event.version, event.botUser, event.guilds, event.sessionId, event.resumeGatewayUrl, diskord)
        }
    }

    override suspend fun handle() {
        diskord.apply {
            gateway.session = sessionId
            gateway.resumeUrl = resumeGatewayUrl
            id = botUser.id

            guildIds.addAll(guilds.map(UnavailableGuild::id))
            unavailableGuilds.putAll(guilds
                .filter { it.unavailable == true }
                .map { Pair(it.id, CompletableDeferred()) })
        }

        if (diskord.initialConnection) {
            InitialReadyEvent(this@ReadyEvent, diskord).handle()
            diskord.initialConnection = false
        }
        call()
    }

}