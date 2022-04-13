package bot.myra.diskord.rest.request.error

open class DiscordRestExceptionBuilder(
    var error: DiscordRestErrors = DiscordRestErrors.UNKNOWN,
    var info: String? = null
) {
    val exception = DiscordRestException(error, info)
}