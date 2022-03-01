package bot.myra.diskord.common.caching

import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

object ChannelCache : Cache<String, ChannelData>() {

    override fun retrieveAsync(key: String): Deferred<ChannelData?> {
        return RestClient.executeAsync(Endpoints.getChannel) {
            arguments { arg("channel.id", key) }
        }
    }

}