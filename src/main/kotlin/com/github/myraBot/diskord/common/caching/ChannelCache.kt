package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient

object ChannelCache : Cache<String, ChannelData>(
    retrieve = { key ->
        RestClient.executeAsync(Endpoints.getChannel) {
            arguments { arg("channel.id", key) }
        }
    }
)