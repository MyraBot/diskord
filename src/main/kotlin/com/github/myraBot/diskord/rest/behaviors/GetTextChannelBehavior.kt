package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.TextChannel
import com.github.myraBot.diskord.common.entityData.channel.ChannelData
import com.github.myraBot.diskord.rest.Endpoints

interface GetTextChannelBehavior {

}

suspend inline fun <reified T> GetTextChannelBehavior.getChannel(id: String): T {
    val data = Endpoints.getChannel.execute { arg("channel.id", id) }
    return when (T::class) {
        TextChannel::class -> TextChannel(data)
        else -> throw IllegalStateException()
    } as T
}