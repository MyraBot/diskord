package bot.myra.diskord.voice.udp

import bot.myra.diskord.voice.udp.packets.PayloadType
import com.codahale.xsalsa20poly1305.SecretBox
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * [Documentation](https://datatracker.ietf.org/doc/html/rfc3550#section-5.1)
 *
 * @property paddingBytes
 * @property payloadType
 * @property sequence
 * @property timestamp
 * @property ssrc
 * @property payload
 */
data class RtpPacket(
    val paddingBytes: Byte,
    val payloadType: PayloadType,
    val sequence: Short,
    val timestamp: Int,
    val ssrc: Int,
    val payload: ByteArray,
) {

    companion object {
        private const val version2Bit: Int = 0x80 // 10000000
        private const val paddingBit: Int = 0x20 // 00100000
        private const val extensionBit: Int = 0x10 // 00010000
        private const val csrcBit: Byte = 0x0F // 1111
        private const val markerBit: Byte = 0x80.toByte() // 10000000
        private const val withoutMarkerBits: Byte = 0x7F // 01111111

        @OptIn(ExperimentalUnsignedTypes::class)
        fun fromPacket(packet: ByteReadPacket, encryption: SecretBox): RtpPacket? {
            val header = packet.copy()
            val nonce = ByteBuffer.allocate(24).apply {
                put(packet.readBytes(12)) // header is 12 bytes long
            }.array()

            /*
            * Bit table
            * 0-1 = RTP protocol version
            * 2   = Do we use padding? Opus uses an internal padding system, so RTP padding is not used.
            * 3   = Do we use extensions?
            * 4-7 = Represent the CC or CSRC count. CSRC is Combined SSRC.
            */
            val hasPadding: Boolean
            val hasExtension: Boolean
            val csrcCount: Byte
            with(header.readByte()) {
                if (toInt() and version2Bit != version2Bit) return null // Wrong version
                hasPadding = toInt() and paddingBit == paddingBit
                hasExtension = toInt() and extensionBit == extensionBit
                csrcCount = this and csrcBit
            }
            val csrcLength: Int = csrcCount * 4

            /*
            * Bit table
            * 0   = marker
            * 1-7 = Payload type
            */
            val isMarker: Boolean
            val payloadType: PayloadType
            with(header.readByte()) {
                isMarker = this and markerBit == markerBit
                payloadType = PayloadType.from(this and withoutMarkerBits)
            }

            val sequence = header.readShort().toUShort()
            val timestamp = header.readInt().toUInt()
            val ssrc = header.readInt().toUInt()

            if (header.remaining <= csrcLength) return null
            val csrcIdentifiers = mutableListOf<UInt>()
            for (i in 0 until csrcCount) csrcIdentifiers.add(header.readUInt())

            var payload = packet.readBytes()
            val paddingBytes: Byte = if (hasPadding) payload.last() else 0
            payload = payload.sliceArray(0 until payload.size - paddingBytes)

            var decryptedAudio = encryption.open(nonce, payload).let {
                if (it.isPresent) it.get() else null
            } ?: return null

            /**
             * [Documentation](https://datatracker.ietf.org/doc/html/rfc3550#section-5.3.1)
             */
            if (hasExtension) {
                with(ByteReadPacket(decryptedAudio)) {
                    discard(Short.SIZE_BYTES) // Skip profile data
                    val countOf32BitWords = readShort()
                    val sizeOf32BitWordsInBits = countOf32BitWords * 32
                    val sizeOf32BitWordsInBytes = sizeOf32BitWordsInBits / Byte.SIZE_BITS

                    discard(sizeOf32BitWordsInBytes)
                    decryptedAudio = readBytes()
                }
            }

            return RtpPacket(paddingBytes, payloadType, sequence.toShort(), timestamp.toInt(), ssrc.toInt(), decryptedAudio)
        }

    }

}