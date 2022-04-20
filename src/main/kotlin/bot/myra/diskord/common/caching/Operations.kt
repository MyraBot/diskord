package bot.myra.diskord.common.caching

typealias ViewCache<V> = () -> List<V>
typealias GetCache<K, V> = (K) -> V?
typealias UpdateCache<V> = (V) -> Unit
typealias RemoveCache<K> = (K) -> Unit
typealias ViewByGuildId<K, V> = (K) -> List<V>?
typealias AssociatedByGuildId<ITEM_ID> = (ITEM_ID) -> String?