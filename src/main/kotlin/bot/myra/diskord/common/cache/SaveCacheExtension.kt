package bot.myra.diskord.common.cache

import bot.myra.diskord.rest.request.Result

suspend fun <K, V> Result<V>.cache(cache: GenericCachePolicy<K, V>): Result<V> {
    value?.let { cache.update(it) }
    return this
}

suspend fun <K, V> Result<List<V>>.cacheEach(cache: GenericCachePolicy<K, V>): Result<List<V>> {
    value?.let { list -> list.forEach { cache.update(it) } }
    return this
}