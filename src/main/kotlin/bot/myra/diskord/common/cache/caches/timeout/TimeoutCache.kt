package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.GenericCachePolicy
import kotlinx.coroutines.*
import java.util.concurrent.ForkJoinPool
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

abstract class TimeoutCache<K, V>(private val expireIn: Duration = 10.minutes) {
    private val timeouts = mutableMapOf<K, Job>()

    suspend fun startExpiry(key: K) {
        stopExpiry(key)
        timeouts[key] = coroutineScope.launch {
            delay(expireIn)
            policy().remove(key)
            println("removed $key of type ${key!!::class.simpleName}")
            println(policy().view().size)
        }
    }

    fun stopExpiry(key: K) {
        timeouts[key]?.cancel()
    }

    abstract fun policy(): GenericCachePolicy<K, V>

}