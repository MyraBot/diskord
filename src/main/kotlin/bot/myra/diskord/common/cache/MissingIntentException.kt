package bot.myra.diskord.common.cache

import bot.myra.diskord.gateway.GatewayIntent

class MissingIntentException(cache: GenericCachePolicy<*, *>, intent: GatewayIntent) : Exception("${cache::class.simpleName} requires the ${intent.name} intent")