package bot.myra.diskord.rest.request.error

data class ModificationException(override val message: String) : Exception()
data class BadReqException(override val message: String) : Exception()
data class MissingPermissionsException(override val message: String) : Exception()
data class RateLimitException(override val message: String) : Exception()
data class NotFoundException(override val message: String) : Exception()