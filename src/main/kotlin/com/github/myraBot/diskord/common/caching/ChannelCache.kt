package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

object ChannelCache : Cache<String, ChannelData>() {

    override fun retrieveAsync(key: String): Deferred<ChannelData?> {
        return RestClient.executeAsync(Endpoints.getChannel) {
            arguments { arg("channel.id", key) }
        }
    }

}