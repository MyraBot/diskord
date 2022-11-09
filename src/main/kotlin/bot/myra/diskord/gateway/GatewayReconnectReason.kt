package bot.myra.diskord.gateway

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#resuming)
 *
 * @property resume Whether to resume the connection.
 * @property cause Description of the reason.
 */
abstract class ReconnectReason(val resume: Boolean, val cause: String) {
    class NoCode : ReconnectReason(true, "No close code found")
    class InvalidSessionResume : ReconnectReason(true, "Invalid session (resumable)")
    class InvalidSession : ReconnectReason(true, "Invalid session")
    class RequestReconnect : ReconnectReason(true, "Received reconnect from discord")
    class UnknownCloseCode: ReconnectReason(false, "Unknown close code")
    class Any(resume: Boolean, cause: String) : ReconnectReason(resume, cause)
}