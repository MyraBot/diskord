package bot.myra.diskord.voice.udp

/**
 * Configuration for audio transmissions
 *
 * @property millisPerPacket The milliseconds worth of audio data sent with each packet.
 * @property sampleRate Samples per second, usually 48kHz.
 */
data class AudioProvidingConfiguration(
    val millisPerPacket: UInt = 20u,
    val sampleRate: UInt = 48_000u,
    val channels: UShort = 2u,
) {
    val secondsPerPacket: Float = millisPerPacket.toFloat() / 1000f
    val millisOfASecond: UInt = (1f / secondsPerPacket).toUInt()

    /**
     * Also referred as "frame size".
     */
    val samplesPerPacket: Int = (sampleRate.toFloat() * secondsPerPacket).toInt()
    val timestampPerPacket: UInt = sampleRate / millisOfASecond
}