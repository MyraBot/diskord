package bot.myra.diskord.rest.request.error.rateLimits

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.request.HttpRequest
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.error.DiscordRestErrors
import bot.myra.diskord.rest.request.error.DiscordRestExceptionBuilder
import kotlinx.coroutines.delay

object RateLimitWorker {

    suspend fun <T> queue(request: HttpRequest<T>, rateLimit: RateLimit,exception:DiscordRestExceptionBuilder): T? {
        val delay = (rateLimit.retryAfter * 1000).toLong()
        if (delay > Diskord.rateLimitThreshold) {
            throw exception.apply {
                error = DiscordRestErrors.RATE_LIMIT
                info = rateLimit.message
            }.exception
        }
        delay(delay)
        return RestClient.executeRequest(request)
    }

}