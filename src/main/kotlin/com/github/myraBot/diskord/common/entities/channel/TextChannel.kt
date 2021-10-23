package com.github.myraBot.diskord.common.entities.channel

import com.github.m5rian.discord.JSON
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.builders.MessageBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class TextChannel(
        @SerialName("id") val id: String,
        @Serializable(with = ChannelSerializer::class) val type: Channel.Type,
        @SerialName("guild_id") internal val guildId: String? = null,
        val position: Int,
        val name: String
) {
    suspend fun send(builder: MessageBuilder.() -> Unit): Message {
        val message = MessageBuilder().apply(builder)
        val json = JSON.encodeToString(message)
        return Endpoints.createMessage.execute(json) { arg("channel.id", id) }
    }
}