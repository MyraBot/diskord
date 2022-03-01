package com.github.myraBot.diskord.gateway.commands

import com.github.myraBot.diskord.common.Diskord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Presence builder, used to update the presence and status of the bot.
 * Used in [Diskord.updatePresence].
 *
 * @property since The unix time (in milliseconds) of when the client went idle, or null if the client is not idle.
 * @property activity The bot's activity.
 * @property status The bots online status.
 * @property afk Whether the bot is afk.
 */
data class Presence(
    var since: Int? = null,
    var activity: Activity? = null,
    val status: Status,
    var afk: Boolean = false
)

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#update-presence-gateway-presence-update-structure)
 * Opt-code data for updating the presence and status of the bot.
 * Can be updated using [Diskord.updatePresence].
 *
 * @property since The unix time (in milliseconds) of when the client went idle, or null if the client is not idle.
 * @property activities The bots activities.
 * @property status The bots online status.
 * @property afk Whether the bot is afk.
 */
@Serializable
data class PresenceUpdate(
    val since: Int? = null,
    var activities: List<Activity> = emptyList(),
    val status: Status,
    var afk: Boolean = false
)

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#activity-object-activity-structure)
 * Activity of the bot. Can be set using [Diskord.updatePresence].
 *
 * @property name The activity's name.
 * @property type A [ActivityType].
 * @property url stream url, is validated when type is [ActivityType.STREAMING].
 */
@Serializable
data class Activity(
    val name: String,
    val type: ActivityType,
    val url: String? = null
)

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#update-presence-status-types)
 * Possible online status types of the bot.
 *
 * @property status Status name.
 */
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

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#activity-object-activity-types)
 * Possible activity types for the bot.
 *
 * @property id Activity id.
 */
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