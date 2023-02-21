package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.voice.VoiceChannelBehavior

data class VoiceChannel(
    override val data: ChannelData,
    override val diskord: Diskord
) : VoiceChannelBehavior, GuildChannelBehavior {
    override val id get() = data.id
    val source get() = data.source
    val guildId get() = data.guildId
    val position get() = data.position
    val name get() = data.name
    val topic get() = data.topic
    val bitrate get() = data.bitrate
    val userLimit get() = data.userLimit
    val parentId get() = data.parentId
    val voiceRegion get() = data.voiceRegion
}