package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Guild(
        val id: String
) {
    suspend fun getMember(id: String) = Endpoints.getGuildMember.execute { arg("user.id", id) }
    suspend fun botMember(): Member = getMember(Endpoints.getBotApplication.execute().id)
    suspend inline fun <reified T> getChannel(id: String): T {
        val serializer = when (T::class) {
            TextChannel::class -> TextChannel.serializer()
            else -> throw IllegalStateException()
        }
        return Endpoints.getChannel.executeWithType(serializer as KSerializer<T>) { arg("channel.id", id) }
    }
}
