package bot.myra.diskord.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.toJsonObj
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.gateway.commands.VoiceUpdate
import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.events.GenericVoiceEvent
import bot.myra.diskord.voice.gateway.events.VoiceEvent
import bot.myra.diskord.voice.gateway.events.impl.VoiceReadyEvent
import bot.myra.diskord.voice.udp.UdpSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.slf4j.LoggerFactory
import kotlin.reflect.full.findAnnotation
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
    val guildId: String,
    val diskord: Diskord,
    val scope: CoroutineScope
) {
    val logger = LoggerFactory.getLogger("Voice-$session")
    private val gateway = VoiceGateway(scope, endpoint, token, session, guildId, diskord)
    val eventDispatcher = gateway.eventDispatcher
    var udp: UdpSocket? = null

    suspend inline fun <reified T : GenericVoiceEvent> onEvent(crossinline callback: (T) -> Unit) {
        val operation = T::class.findAnnotation<VoiceEvent>()?.operation ?: return
        scope.launch {
            eventDispatcher
                .filter { it.operation == operation }
                .mapNotNull { it as T? }
                .collect { callback(it) }
        }
    }

    suspend inline fun <reified T : GenericVoiceEvent> awaitEvent(timeout: Duration = 5.seconds): T? {
        val operation = T::class.findAnnotation<VoiceEvent>()?.operation ?: return null
        return withTimeoutOrNull(timeout) {
            eventDispatcher.first { it.operation == operation } as T?
        }
    }

    suspend fun openConnection() {
        gateway.connect()
        val connectionDetails = gateway.awaitEvent<VoiceReadyEvent>() ?: TODO()
        udp = UdpSocket(scope, gateway, connectionDetails).apply { openSocketConnection() }
        logger.debug("Successfully created voice connection for $guildId")
    }

    /**
     * Disconnects form all connections.
     * This does not change the voice state.
     */
    suspend fun disconnect() {
        gateway.disconnect()
        udp?.disconnect()
    }

    suspend fun leave() {
        val state = VoiceUpdate(
            guildId = guildId,
            channelId = null,
            selfMute = false,
            selfDeaf = false
        )
        val op = OpPacket(
            op = 4,
            d = state.toJsonObj(true),
            s = null,
            t = null
        )
        diskord.gateway.send(op)
    }

}