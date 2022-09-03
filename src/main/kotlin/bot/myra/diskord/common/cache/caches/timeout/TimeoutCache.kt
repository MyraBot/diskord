package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.GenericCachePolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.concurrent.ForkJoinPool
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

abstract class TimeoutCache<K, V>(private val expireIn: Duration = 10.seconds) {
    private val interval = 10.seconds
    internal val map = mutableMapOf<K, TimeoutCacheValue<V>>()

    init {
        coroutineScope.launch { runCheck() }
    }

    private suspend fun runCheck() {
        val estimatedFinishInstant = Clock.System.now() + interval
        map.forEach { (k, v) ->
            if (v.expireAt <= Clock.System.now()) policy().remove(k)
        }
        delay(estimatedFinishInstant.minus(Clock.System.now()))
        runCheck()
    }

    abstract fun policy(): GenericCachePolicy<K, V>

}