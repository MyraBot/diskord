package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.behaviors.channel.TextChannelBehavior

class TextChannel(
    override val data: ChannelData,
    override val diskord: Diskord
) : Entity, GuildChannelBehavior, TextChannelBehavior {
    override val id get() = data.id
    val source get() = data.source
    val guildId get() = data.guildId
    val position get() = data.position
    val name get() = data.name
    val topic get() = data.topic
    val nsfw get() = data.nsfw
    val lastMessageId get() = data.lastMessageId
    val bitrate get() = data.bitrate
    val userLimit get() = data.userLimit
    val rateLimitPerUser get() = data.rateLimitPerUser
    val recipients get() = data.recipients
    val icon get() = data.icon
    val ownerId get() = data.ownerId
    val applicationId get() = data.applicationId
    val parentId get() = data.parentId
    val lastPinTimestamp get() = data.lastPinTimestamp
    val voiceRegion get() = data.voiceRegion
}