package bot.myra.diskord.voice.udp

import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.commands.ProtocolDetails
import bot.myra.diskord.voice.gateway.commands.SelectProtocol
import bot.myra.diskord.voice.gateway.models.ConnectionReadyPayload
import bot.myra.diskord.voice.gateway.models.Operations
import bot.myra.diskord.voice.gateway.models.SessionDescriptionPayload
import bot.myra.diskord.voice.udp.packets.PayloadType
import com.codahale.xsalsa20poly1305.SecretBox
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Udp socket which sends the audio data.
 *
 * @property gateway It's [VoiceGateway] client.
 * @property connectDetails Information to connect.
 */
class UdpSocket(
    private val scope: CoroutineScope,
    val gateway: VoiceGateway,
    val connectDetails: ConnectionReadyPayload,
) {
    private val logger: Logger = LoggerFactory.getLogger(UdpSocket::class.java)

    private lateinit var socket: ConnectedDatagramSocket
    lateinit var remoteAddress: InetSocketAddress
    private val voiceServer: SocketAddress = InetSocketAddress(connectDetails.ip, connectDetails.port)

    private lateinit var encryption: SecretBox
    lateinit var audioProvider: AudioProvider
    val audioReceiver: Channel<RtpPacket> = Channel()

    suspend fun openSocketConnection() {
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).udp().connect(remoteAddress = voiceServer)
        remoteAddress = discoverIp()
        val selectProtocol = SelectProtocol("udp", ProtocolDetails(remoteAddress.hostname, remoteAddress.port, "xsalsa20_poly1305"))
        gateway.send(selectProtocol)

        val secretKey = gateway.eventDispatcher
            .first { it.op == Operations.SESSION_DESCRIPTION.code }
            .let { it.d ?: throw IllegalStateException() }
            .let { JSON.decodeFromJsonElement<SessionDescriptionPayload>(it) }
            .secretKey
        encryption = SecretBox(secretKey.toUByteArray().toByteArray())

        audioProvider = AudioProvider(
            gateway = gateway,
            socket = this,
            encryption = encryption,
            scope = scope
        )
        audioProvider.start()

        scope.launch {
            socket.incoming.consumeAsFlow()
                .filter { voiceServer == it.address }
                .mapNotNull { RtpPacket.fromPacket(it.packet, encryption) }
                .filter { it.payloadType == PayloadType.Audio }
                .collect { audioReceiver.send(it) }
        }
    }

    private suspend fun discoverIp(): InetSocketAddress {
        send {
            writeInt(connectDetails.ssrc) // Ssrc
            writeFully(ByteArray(66)) // Address and port
        }

        return with(socket.incoming.receive().packet) {
            discard(4)
            val ip = String(readBytes(64)).trimEnd(0.toChar())
            val port = readUShort().toInt()
            logger.debug("Ip discovery successful, connecting to ${ip}:${port}")
            InetSocketAddress(ip, port)
        }
    }

    fun disconnect() {
        audioProvider.stop()
        socket.close()
        logger.debug("Closed udp socket")
    }

    suspend fun send(builder: BytePacketBuilder.() -> Unit) {
        val datagram = Datagram(buildPacket(block = builder), voiceServer)
        socket.send(datagram)
    }

}