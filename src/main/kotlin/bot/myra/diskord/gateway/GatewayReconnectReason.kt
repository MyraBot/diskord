package bot.myra.diskord.gateway

enum class GatewayReconnectReason(
    val resume: Boolean,
    val cause: String
) {
    INVALID_SESSION_RESUME(true, "Invalid session (is resumable)"),
    INVALID_SESSION(false, "Invalid session"),
    RECONNECT( true, "Received reconnect from discord");
}