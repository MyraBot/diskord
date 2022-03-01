package bot.myra.diskord.rest.request.error

class EntityModifyException(message: String?) : Exception(message)
class BadReqException(message: String?) : Exception(message)
class RateLimitException(message: String?) : Exception(message)