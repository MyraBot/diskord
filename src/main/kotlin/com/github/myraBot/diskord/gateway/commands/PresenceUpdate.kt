package com.github.myraBot.diskord.gateway.commands

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class Presence(
    val since: Int? = null,
    var activity: Activity? = null,
    val status: Status,
    var afk: Boolean = false
)

@Serializable
data class PresenceUpdate(
    val since: Int? = null,
    var activities: List<Activity> = emptyList(),
    val status: Status,
    var afk: Boolean = false
)

@Serializable
data class Activity(
    val name: String,
    val type: ActivityType,
    val url: String? = null
)

@Serializable(with = Status.Serializer::class)
enum class Status(val status: String) {
    ONLINE("online"),
    DND("dnd"),
    IDLE("idle"),
    INVISIBLE("invisible"),
    OFFLINE("offline");

    internal object Serializer : KSerializer<Status> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Status", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: Status) = encoder.encodeString(value.status)
        override fun deserialize(decoder: Decoder): Status = decoder.decodeString().let { status -> values().first { it.status == status } }
    }
}

@Serializable(with = ActivityType.Serializer::class)
enum class ActivityType(val id: Int) {
    GAME(0),
    STREAMING(1),
    LISTENING(2),
    WATCHING(3),
    CUSTOM(4),
    COMPETING(5);

    internal object Serializer : KSerializer<ActivityType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ActivityType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: ActivityType) = encoder.encodeInt(value.id)
        override fun deserialize(decoder: Decoder): ActivityType = decoder.decodeInt().let { id -> values().first { it.id == id } }
    }
}