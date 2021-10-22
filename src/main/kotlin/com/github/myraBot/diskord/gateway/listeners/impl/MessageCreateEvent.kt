package com.github.myraBot.diskord.gateway.listeners.impl

import com.github.m5rian.discord.JSON
import com.github.m5rian.discord.objects.entities.User
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MessageCreateSerializer::class)
data class MessageCreateEvent(
        val message: Message,
        val user: User
) : Event()

private object MessageCreateSerializer : KSerializer<MessageCreateEvent> {
    override val descriptor: SerialDescriptor get() = PrimitiveSerialDescriptor("MessageCreate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MessageCreateEvent {
        println("String=" + decoder.decodeString())
        val message = JSON.decodeFromString<Message>(decoder.decodeString())
        val user = JSON.decodeFromString<User>(decoder.decodeString())
        return MessageCreateEvent(message, user)
    }

    override fun serialize(encoder: Encoder, value: MessageCreateEvent) {
        TODO("Not yet implemented")
    }

}