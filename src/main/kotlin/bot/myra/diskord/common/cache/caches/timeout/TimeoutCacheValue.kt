package bot.myra.diskord.common.cache.caches.timeout

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

data class TimeoutCacheValue<T>(
    var value: T,
    var expireAt: Instant = Clock.System.now().plus(10.seconds)
) {
    fun updateExpiry() {
        expireAt = Clock.System.now().plus(10.seconds)
    }
}