package bot.myra.diskord.gateway

enum class GatewayClosedReason(
    var code: Short?,
    val resume: Boolean,
    val cause: String,
    val exception: Boolean = false
) {
    UNKNOWN_ERROR(4000, true, "Unknown error occurred"),
    UNKNOWN_OPCODE(4001, false, "Sent an unknown opcode", true),
    DECODE_ERROR(4002, false, "Sent an invalid payload"),
    NOT_AUTHENTICATED(4003, false, "Sent a payload before identifying", true),
    AUTHENTICATION_FAILED(4004, false, "Invalid token", true),
    ALREADY_AUTHENTICATED(4005, false, "You sent more than one identify payload"),
    INVALID_SEQUENCE(4007, false, "Invalid sequence when resuming"),
    RATE_LIMITED(4008, false, "Rate limited"),
    SESSION_TIMED_OUT(4009, false, "Session timed out"),
    INVALID_SHARD(4010, false, "Invalid shard", true),
    SHARDING_REQUIRED(4011, false, "Because of too many guilds you have to shard your sessions", true),
    INVALID_API_VERSION(4012, false, "Invalid gateway version", true),
    INVALID_INTENTS(4013, false, "Your specified intents are invalid", true),
    DISALLOWED_INTENTS(4014, false, "You may have tried to specify an intent that you have not enabled or are not approved for", true),

    // Custom close reasons - used when we close the connection
    RECEIVED_INVALID_SESSION_RESUME(4500, true, "Invalid session (does resumable)"),
    RECEIVED_INVALID_SESSION(4501, false, "Invalid session"),
    RECEIVED_RECONNECT(4502, true, "Received reconnect from discord"),

    UNKNOWN(null, true, "Unknown error code");

    companion object {
        fun fromCode(code: Short): GatewayClosedReason =
            values().firstOrNull { it.code == code } ?: UNKNOWN.apply { this.code = code }
    }

}