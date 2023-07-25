package bot.myra.diskord.voice.udp

import bot.myra.diskord.voice.udp.packets.IncomingUserAudioPacket
import bot.myra.diskord.voice.udp.packets.RtpPacket
import bot.myra.diskord.voice.udp.packets.ogg.AudioCodecHandler
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.system.measureTimeMillis

data class CombinedAudioFrame(
    val users: Set<String>,
    val payloads: List<ByteArray>,
    val combinedAudio: ByteArray
)

class CombinedAudioReceiver(
    private val scope: CoroutineScope,
    private val configuration: AudioProvidingConfiguration,
    private val incoming: Channel<IncomingUserAudioPacket>
) {
    private val userPackets = mutableMapOf<String, RtpPacket>()
    private val encoder = AudioCodecHandler.create(configuration)
    private val decoder = mutableMapOf<String, AudioCodecHandler>()

    private val emitter = MutableSharedFlow<CombinedAudioFrame>()
    val outgoing = emitter.asSharedFlow()

    init {
        scope.launch {
            incoming.receiveAsFlow().collect {
                if (it.userId == null) return@collect
                userPackets[it.userId] = it.packet
            }
        }

        scope.launch {
            while (true) {
                val executionTime = measureTimeMillis { combinePackets() }
                val durationToWaitToReachIdealTime = configuration.millisPerPacket.toLong() - executionTime
                val wait = max(0, durationToWaitToReachIdealTime)
                delay(wait)
            }
        }
    }

    private suspend fun combinePackets() {
        val queuedPackets = userPackets.toMap()
        if (queuedPackets.isEmpty()) return
        userPackets.clear()

        val pcmPayloads = queuedPackets.mapNotNull {
            val codecHandler = decoder.getOrPut(it.key) { AudioCodecHandler.create(configuration) }
            codecHandler.decodeOpus(it.value.payload)
        }
        val maxLength = pcmPayloads.maxOfOrNull { it.limit() } ?: 0
        val combinedAudio = pcmPayloads
            .map { it.limit(maxLength) }
            .reduce { combined, new ->
                for (i in 0 until combined.limit()) {
                    val mixed = mixSample(combined.get(i), new.get(i))
                    combined.put(i, mixed)
                }
                combined
            }

        val opus = encoder.encode(combinedAudio) ?: return
        val combinedFrame = CombinedAudioFrame(queuedPackets.keys, queuedPackets.values.map { it.payload }, opus.moveToByteArray())
        emitter.emit(combinedFrame)
    }

    private fun mixSample(sample1: Short, sample2: Short): Short {
        val result = (sample1.toInt() + sample2.toInt())
        val range = Short.MIN_VALUE..Short.MAX_VALUE

        return when {
            result > range.last  -> range.last.toShort()
            result < range.first -> range.first.toShort()
            else                 -> result.toShort()
        }
    }

}