package bot.myra.diskord.common.entities.channel.text

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.channel.GuildChannelBehavior

class TextChannel(
    override val data: ChannelData,
    override val diskord: Diskord
) : GenericTextChannel(data, diskord), GuildChannelBehavior {
    val position get() = data.position
    val bitrate get() = data.bitrate
    val userLimit get() = data.userLimit
    val rateLimitPerUser get() = data.rateLimitPerUser
    val recipients get() = data.recipients
    val icon get() = data.icon
    val ownerId get() = data.ownerId
    val applicationId get() = data.applicationId
    val parentId get() = data.parentId
    val voiceRegion get() = data.voiceRegion
}