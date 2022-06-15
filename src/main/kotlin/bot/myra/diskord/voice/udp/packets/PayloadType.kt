package bot.myra.diskord.voice.udp.packets

/**
 * A guesstimated list of known Discord RTP payloads.
 *
 * @author Kord
 */
enum class PayloadType(var raw: Byte) {
    Alive(0x37.toByte()),
    Audio(0x78.toByte()),
    Unknown(0.toByte());

    companion object {
        fun from(value: Byte): PayloadType = when (value) {
            0x37.toByte() -> Alive
            0x78.toByte() -> Audio
            else          -> Unknown.apply { raw = value }
        }
    }
}