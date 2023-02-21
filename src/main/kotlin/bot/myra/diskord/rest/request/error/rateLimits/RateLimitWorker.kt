package bot.myra.diskord.rest.request.error.rateLimits

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.request.HttpRequest
import bot.myra.diskord.rest.request.Result
import bot.myra.diskord.rest.request.error.RateLimitException
import kotlinx.coroutines.delay

class RateLimitWorker(
    val diskord: Diskord
) {

    suspend fun <T> queue(request: HttpRequest<T>, rateLimit: RateLimit): Result<T> {
        val delay = (rateLimit.retryAfter * 1000).toLong()
        if (delay > diskord.rateLimitThreshold) throw RateLimitException(rateLimit.message)
        delay(delay)
        return diskord.rest.execute(request)
    }

}