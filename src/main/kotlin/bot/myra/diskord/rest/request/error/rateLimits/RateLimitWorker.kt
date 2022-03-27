package bot.myra.diskord.rest.request.error.rateLimits

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.request.HttpRequest
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.error.RateLimitException
import kotlinx.coroutines.delay

object RateLimitWorker {

    suspend fun <T> queue(request: HttpRequest<T>, rateLimit: RateLimit): T? {
        val delay = (rateLimit.retryAfter * 1000).toLong()
        if (delay > Diskord.rateLimitThreshold) throw RateLimitException(rateLimit.message)
        delay(delay)
        return RestClient.executeRequest(request)
    }

}