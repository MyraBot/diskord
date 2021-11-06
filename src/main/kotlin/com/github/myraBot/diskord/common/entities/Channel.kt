package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.rest.behaviors.Entity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Channel(
        override val id: String,
        @Serializable(with = Type.Serializer::class) val type: Type,
        @SerialName("guild_id") internal val guildId: String? = null,
        val position: Int,
        val name: String
) : Entity {
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
        GUILD_STAGE_VOICE(13);
        
        internal object Serializer : KSerializer<Type> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ChannelType", PrimitiveKind.INT)
            override fun serialize(encoder: Encoder, value: Type) = encoder.encodeInt(value.id)
            override fun deserialize(decoder: Decoder): Type = values().first { it.id == decoder.decodeInt() }
        }
    }
}


