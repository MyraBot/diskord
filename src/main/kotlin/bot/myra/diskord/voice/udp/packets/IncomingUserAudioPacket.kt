package bot.myra.diskord.voice.udp.packets

data class IncomingUserAudioPacket(
    val userId: String?,
    val packet: RtpPacket
)