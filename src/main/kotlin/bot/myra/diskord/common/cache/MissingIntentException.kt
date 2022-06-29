package bot.myra.diskord.common.cache

import bot.myra.diskord.gateway.GatewayIntent
import kotlin.reflect.KClass

class MissingIntentException(cache: KClass<*>, intent: GatewayIntent) : Exception("${cache.simpleName} requires the ${intent.name} intent")