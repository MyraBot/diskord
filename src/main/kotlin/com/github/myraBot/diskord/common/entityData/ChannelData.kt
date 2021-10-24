package com.github.myraBot.diskord.common.entityData.channel

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ChannelData(
        val id: String,
        @Serializable(with = ChannelSerializer::class) val type: Type,
        @SerialName("guild_id") internal val guildId: String? = null,
        val position: Int,
        val name: String
) {
    enum class Type(val id: Int) {
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
        GUILD_STAGE_VOICE(13)
    }
}

object ChannelSerializer : KSerializer<ChannelData.Type> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ChannelType", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: ChannelData.Type) = encoder.encodeInt(value.id)
    override fun deserialize(decoder: Decoder): ChannelData.Type = ChannelData.Type.values().first { it.id == decoder.decodeInt() }

}


