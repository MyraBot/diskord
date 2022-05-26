package bot.myra.diskord.voice.gateway.models

enum class VoiceSocketClosedReason(val code: Short) {
    UNKNOWN_OPCODE(4001),
    FAILED_PAYLOAD_DECODING(4002),
    NOT_AUTHENTICATED(4003),
    AUTHENTICATION_FAILED(4004),
    ALREADY_AUTHENTICATED(4005),
    SESSION_INVALID(4006),
    SESSION_TIMEOUT(4009),
    SERVER_NOT_FOUND(4011),
    UNKNOWN_PROTOCOL(4012),
    DISCONNECTED(4014),
    VOICE_SERVER_CRASHED(4015),
    UNKNOWN_ENCRYPTION_MODE(4016);

    companion object {
        fun fromCode(code: Short): VoiceSocketClosedReason = values().first { it.code == code }
    }

}