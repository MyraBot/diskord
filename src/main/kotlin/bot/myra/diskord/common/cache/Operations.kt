package bot.myra.diskord.common.cache

typealias ViewCache<V> = suspend () -> List<V>
typealias GetCache<K, V> = suspend (K) -> V?
typealias UpdateCache<V> = suspend (V) -> Unit
typealias RemoveCache<K> = suspend (K) -> Unit
typealias ViewByGuildId<K, V> = suspend (K) -> List<V>?
typealias AssociatedByGuildId<ITEM_ID> = suspend (ITEM_ID) -> String?