package bot.myra.diskord.voice.udp.packets.ogg

import bot.myra.diskord.voice.udp.AudioProvidingConfiguration
import club.minnced.opus.util.OpusLibrary
import com.sun.jna.Native
import com.sun.jna.ptr.PointerByReference
import tomp2p.opuswrapper.Opus
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class AudioCodecHandler(
    private val configuration: AudioProvidingConfiguration,
    private val decoder: PointerByReference,
    private val encoder: PointerByReference,
) {

    companion object {
        fun create(configuration: AudioProvidingConfiguration): AudioCodecHandler {
            try {
                System.loadLibrary("opus")
            } catch (unsatisfiedLink: UnsatisfiedLinkError) {
                try {
                    val file = Native.extractFromResourcePath("opus")
                    System.load(file.absolutePath)
                } catch (e: Exception) {
                    unsatisfiedLink.printStackTrace()
                    e.printStackTrace()
                }
            }

            if (!OpusLibrary.isInitialized()) {
                val audioSupported = OpusLibrary.loadFromJar()
                if (!audioSupported) throw Exception("Unable to load Opus")
            }

            val decoderResult = IntBuffer.allocate(1)
            val sampleRate = configuration.sampleRate.toInt()
            val channels = configuration.channels.toInt()

            val decoder = Opus.INSTANCE.opus_decoder_create(sampleRate, channels, decoderResult)
            if (decoderResult.get() != Opus.OPUS_OK && decoder == null) {
                throw IllegalStateException("Failed to create opus decoder")
            }

            val encoderResult = IntBuffer.allocate(1)
            val encoder = Opus.INSTANCE.opus_encoder_create(sampleRate, channels, Opus.OPUS_APPLICATION_RESTRICTED_LOWDELAY, encoderResult)
            if (encoderResult.get() != Opus.OPUS_OK && encoder == null) {
                throw IllegalStateException("Failed to create opus encoder")
            }

            return AudioCodecHandler(configuration, decoder, encoder)
        }
    }

    fun decodeOpus(opus: ByteArray): ShortBuffer? {
        val pcm = ShortBuffer.allocate(configuration.samplesPerPacket * configuration.channels.toInt() * Short.SIZE_BYTES)
        val result = Opus.INSTANCE.opus_decode(decoder, opus, opus.size, pcm, configuration.samplesPerPacket, 0)

        if (result < 0) {
            handleDecodeError(result)
            return null
        }

        pcm.position(result)
        pcm.flip()
        return pcm
    }

    fun encode(pcm: ShortBuffer): ByteBuffer? {
        val opus = ByteBuffer.allocate(configuration.samplesPerPacket * configuration.channels.toInt() * Short.SIZE_BYTES)
        val result = Opus.INSTANCE.opus_encode(encoder, pcm, pcm.remaining(), opus, opus.remaining())

        if (result < 0) {
            handleEncodeError(result)
            return null
        }

        opus.position(result)
        opus.flip()
        return opus
    }

    private fun handleDecodeError(result: Int) {
        var b = "Decoder failed to decode audio from user with code "
        when (result) {
            Opus.OPUS_BAD_ARG          -> b += "OPUS_BAD_ARG"
            Opus.OPUS_BUFFER_TOO_SMALL -> b += "OPUS_BUFFER_TOO_SMALL"
            Opus.OPUS_INTERNAL_ERROR   -> b += "OPUS_INTERNAL_ERROR"
            Opus.OPUS_INVALID_PACKET   -> b += "OPUS_INVALID_PACKET"
            Opus.OPUS_UNIMPLEMENTED    -> b += "OPUS_UNIMPLEMENTED"
            Opus.OPUS_INVALID_STATE    -> b += "OPUS_INVALID_STATE"
            Opus.OPUS_ALLOC_FAIL       -> b += "OPUS_ALLOC_FAIL"
            else                       -> b += result
        }
        // TODO use logger
        println(b)
    }

    private fun handleEncodeError(result: Int) {
        var b = "Encoder failed to decode audio from user with code "
        when (result) {
            Opus.OPUS_BAD_ARG          -> b += "OPUS_BAD_ARG"
            Opus.OPUS_BUFFER_TOO_SMALL -> b += "OPUS_BUFFER_TOO_SMALL"
            Opus.OPUS_INTERNAL_ERROR   -> b += "OPUS_INTERNAL_ERROR"
            Opus.OPUS_INVALID_PACKET   -> b += "OPUS_INVALID_PACKET"
            Opus.OPUS_UNIMPLEMENTED    -> b += "OPUS_UNIMPLEMENTED"
            Opus.OPUS_INVALID_STATE    -> b += "OPUS_INVALID_STATE"
            Opus.OPUS_ALLOC_FAIL       -> b += "OPUS_ALLOC_FAIL"
            else                       -> b += result
        }
        // TODO use logger
        println(b)
    }

}