package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.promises.Promise

object ChannelCache : Cache<String, ChannelData>(
    retrieve = { key ->
        Promise.of(Endpoints.getChannel) { arg("channel.id", key) }
    }
)