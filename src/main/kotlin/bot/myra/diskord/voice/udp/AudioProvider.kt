package bot.myra.diskord.voice.udp

import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.models.SpeakingPayload
import bot.myra.diskord.voice.udp.packets.PayloadType
import com.codahale.xsalsa20poly1305.SecretBox
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import kotlin.math.max

class AudioProvider(
    private val gateway: VoiceGateway,
    private val socket: UdpSocket,
    private val config: AudioProvidingConfiguration = AudioProvidingConfiguration(),
    private val encryption: SecretBox,
    private val scope: CoroutineScope
) {
    private var provider: Job? = null
    private val queuedFrames = mutableListOf<ByteArray>()
    private var sentPackets: Short = 0

    private var speaking = false
    private var silenceFrames = 5

    fun stop() {
        provider?.cancel("Closed by user")
        provider = null
    }

    fun provide(bytes: ByteArray) {
        queuedFrames.add(bytes)
    }

    /**
     * Starts sending audio frames to discord.
     * The frames get pulled from the [queuedFrames].
     */
    fun start() {
        this.provider = scope.launch {
            var idealFrameTimestamp = System.currentTimeMillis()
            while (isActive) {
                sendChunk()
                idealFrameTimestamp += config.millisPerPacket.toLong()
                val durationToWaitToReachIdealTime = idealFrameTimestamp - System.currentTimeMillis() // Calculate the theory millis to wait to reach the ideal frame timestamp
                val wait = max(0, durationToWaitToReachIdealTime) // We don't want negative delay
                delay(wait)
            }
        }
    }

    private suspend fun sendChunk() {
        val data = if (queuedFrames.isEmpty()) {
            provideNullFrame()
        } else {
            if (!speaking) {
                startSpeaking() // First audio frame ➜ tell Discord that we want tos peak
            }
            val bytes = queuedFrames.reduce { all, current -> all + current }
            queuedFrames.clear()
            AudioFrame.fromBytes(bytes)
        } ?: return
        sendAudioPacket(data)
        sentPackets++
    }

    private suspend fun startSpeaking() {
        silenceFrames = 5 // Reset silent frames
        speaking = true
        gateway.send(SpeakingPayload(5, socket.connectDetails.ssrc))
    }

    private suspend fun stopSpeaking() {
        speaking = false
        gateway.send(SpeakingPayload(0, socket.connectDetails.ssrc))
    }

    /**
     * Provides an [AudioFrame] from null.
     *
     * @return Returns [AudioFrame.Silence] or null.
     */
    private suspend fun provideNullFrame(): AudioFrame? {
        // We are sending silent frames and haven't completed sending all 5 of them
        return if (silenceFrames > 0) {
            silenceFrames--
            AudioFrame.Silence
        } else {
            // After we stopped speaking & after we sent ALL silent frames, we can tell Discord that we want to stop speaking
            if (speaking) stopSpeaking()
            null
        }
    }

    private suspend fun sendAudioPacket(frame: AudioFrame) {
        socket.send {
            val header = writeHeader() // Rtp header
            val nonce = ByteBuffer.allocate(24).apply { put(header) }.array()
            writeFully(encryptBytes(frame.bytes, nonce)) // Encrypted opus audio
        }
    }

    private fun BytePacketBuilder.writeHeader(): ByteArray {
        val header = ByteBuffer.allocate(12).apply {
            put(0x80.toByte()) // Version + Flags
            put(PayloadType.Audio.raw) // Payload type
            putShort(sentPackets) // Sequence, the count on how many packets have been sent yet
            putInt(sentPackets.toInt() * config.timestampPerPacket.toInt()) // Timestamp
            putInt(socket.connectDetails.ssrc) // SSRC
        }
        writeFully(header.array())
        return header.array()
    }

    private fun encryptBytes(bytes: ByteArray, nonce: ByteArray) = encryption.seal(nonce, bytes)

}