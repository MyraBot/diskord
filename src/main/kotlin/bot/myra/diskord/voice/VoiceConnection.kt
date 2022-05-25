package bot.myra.diskord.voice

import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.models.ConnectionReadyPayload
import bot.myra.diskord.voice.gateway.models.Operations
import bot.myra.diskord.voice.udp.UdpSocket
import bot.myra.kommons.debug
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Voice connection
 *
 * @property endpoint
 * @property session
 * @property token Voice connection token
 * @property guildId
 */
class VoiceConnection(
    val endpoint: String,
    val session: String,
    val token: String,
    val guildId: String
) {
    private val gateway = VoiceGateway(endpoint, token, session, guildId)
    var udp: UdpSocket? = null

    suspend fun openConnection() {
        gateway.connect()
        val connectionDetails = gateway.eventDispatcher
            .first { it.op == Operations.READY.code }
            .let { it.d ?: throw IllegalStateException("Invalid voice ready payload") }
            .let { JSON.decodeFromJsonElement<ConnectionReadyPayload>(it) }
        udp = UdpSocket(gateway, connectionDetails).apply { openSocketConnection() }
        debug(this::class) { "Successfully created voice connection for $guildId" }
    }

}