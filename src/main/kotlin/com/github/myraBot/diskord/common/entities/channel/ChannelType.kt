package com.github.myraBot.diskord.common.entities.channel

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ChannelType.Serializer::class)
enum class ChannelType(val id: Int) {
    GUILD_TEXT(0),
    DM(1),
    GUILD_VOICE(2),
    GROUP_DM(3),
    GUILD_CATEGORY(4),
    GUILD_NEWS(5),
    GUILD_STORE(6),
    GUILD_NEWS_THREAD(10),
    GUILD_PUBLIC_THREAD(11),
    GUILD_PRIVATE_THREAD(12),
    GUILD_STAGE_VOICE(13);

    object Serializer : KSerializer<ChannelType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ChannelType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: ChannelType) = encoder.encodeInt(value.id)
        override fun deserialize(decoder: Decoder): ChannelType = decoder.decodeInt().let { type -> values().first { it.id == type } }
    }
}