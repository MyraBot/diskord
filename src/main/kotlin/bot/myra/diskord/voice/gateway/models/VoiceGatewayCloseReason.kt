package bot.myra.diskord.voice.gateway.models

enum class VoiceGatewayCloseReason(var code: Short?, val reconnect: Boolean, val cause: String) {
    UNKNOWN_OPCODE(4001, false, "You sent an invalid opcode"),
    FAILED_PAYLOAD_DECODING(4002, true, "You sent an invalid identify payload"),
    NOT_AUTHENTICATED(4003, false, "You sent a payload before identifying"),
    AUTHENTICATION_FAILED(4004, false, "Your token is invalid"),
    ALREADY_AUTHENTICATED(4005, false, "You sent more than one identify payload"),
    SESSION_INVALID(4006, true, "Your sessions is no longer valid"),
    SESSION_TIMEOUT(4009, true, "Your session timed out"),
    SERVER_NOT_FOUND(4011, false, "Unknown guild"),
    UNKNOWN_PROTOCOL(4012, false, "Unrecognized protocol"),
    DISCONNECTED(4014, false, "The voice channel was deleted / you were kicker / the voice server changed / main gateway session dropped"),
    VOICE_SERVER_CRASHED(4015, true, "Discord fucked up (their server crashed)"),
    UNKNOWN_ENCRYPTION_MODE(4016, false, "Unrecognized encryption"),
    UNKNOWN(null, true, "Unknown error");

    companion object {
        fun fromCode(code: Short?): VoiceGatewayCloseReason =
            values().firstOrNull { it.code == code } ?: UNKNOWN.apply { this.code = code }
    }

}