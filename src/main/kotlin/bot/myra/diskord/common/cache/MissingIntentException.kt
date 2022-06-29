package bot.myra.diskord.common.cache

import bot.myra.diskord.gateway.GatewayIntent
import kotlin.reflect.KClass

/**
 * Gets thrown when a cache is used, which requires a specific intent.
 * Either disable the cache or enable the intent.
 *
 * @param cache The superclass of the cache.
 * @param intent The intent which is required for the cache.
 */
class MissingIntentException(cache: KClass<*>, intent: GatewayIntent) : Exception("${cache.simpleName} requires the ${intent.name} intent")