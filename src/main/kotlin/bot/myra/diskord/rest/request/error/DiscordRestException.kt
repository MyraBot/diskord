package bot.myra.diskord.rest.request.error

class DiscordRestException(
    val error: DiscordRestErrors,
    val info: String?
) : Exception()