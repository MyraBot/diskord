package diskord.common.caching

import diskord.common.entities.guild.Guild
import diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking


object GuildCache : Cache<String, Guild>() {

    var ids: MutableList<String> = mutableListOf()

    override fun retrieve(key: String): Guild? {
        return runBlocking {
            Endpoints.getGuild.execute { arg("guild.id", key) }
        }
    }

    override fun update(value: Guild) {
        map[value.id] = value
    }
}