package bot.myra.diskord.voice.udp.packets

data class AudioFrame(val bytes: ByteArray) {

    companion object {
        val Silence: AudioFrame = AudioFrame(byteArrayOf(0xFC.toByte(), 0xFF.toByte(), 0xFE.toByte()))
        fun fromBytes(bytes: ByteArray): AudioFrame = AudioFrame(bytes)
    }

}
