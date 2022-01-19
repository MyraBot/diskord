package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Optional
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.rest.behaviors.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/channel#channel-object-channel-structure)
 *
 * Represents any channel. Can be cast to any specific channel by parsing an instance
 * to the constructor of the wanted object.
 *
 * @property id Id of the channel
 * @property source Channel source type.
 * @property guildId Id of guild.
 * @property position Sorting position.
 * @property name Name of the channel
 * @property topic The topic of the channel.
 * @property nsfw Whether the channel is nsfw.
 * @property lastMessageId The id of the last sent message. This must not be an existing message anymore.
 * @property bitrate Bitrate of a voice channel.
 * @property userLimit User limit of a voice channel.
 * @property rateLimitPerUser Amount of seconds, a user has to wait before sending another message.
 * @property recipients Recipients of a group or dm.
 * @property icon The icon hash.
 * @property ownerId The creator id of a group dm or thread.
 * @property applicationId The application of the group dm, if it's bot-created.
 * @property parentId Id of the parent category for a channel. If [source] is a thread, it's the id of the text channel this thread was created.
 * @property lastPinTimestamp When the last pinned message was pinned.
 * @property voiceRegion The voice region id for the voice channel, automatic when set to null.
 */
@Serializable
data class ChannelData(
        override val id: String,
        @SerialName("type") val source: ChannelType,
        @SerialName("guild_id") val guildId: Optional<String> = Optional.Missing(),
        val position: Optional<Int> = Optional.Missing(),
        val name: Optional<String> = Optional.Missing(),
        val topic: Optional<String?> = Optional.Missing(),
        val nsfw: Optional<Boolean> = Optional.Missing(),
        @SerialName("last_message_id") val lastMessageId: Optional<String?> = Optional.Missing(),
        val bitrate: Optional<Int> = Optional.Missing(),
        @SerialName("user_limit") val userLimit: Optional<Int> = Optional.Missing(),
        @SerialName("rate_limit_per_user") val rateLimitPerUser: Optional<Int> = Optional.Missing(),
        val recipients: Optional<List<User>> = Optional.Missing(),
        val icon: Optional<String?> = Optional.Missing(),
        @SerialName("owner_id") val ownerId: Optional<String> = Optional.Missing(),
        @SerialName("application_id") val applicationId: Optional<String> = Optional.Missing(),
        @SerialName("parent_id") val parentId: Optional<String?> = Optional.Missing(),
        @SerialName("last_pin_timestamp") val lastPinTimestamp: Optional<String?> = Optional.Missing(),
        @SerialName("rtc_region") val voiceRegion: Optional<String?> = Optional.Missing()
) : Entity