package bot.myra.diskord.rest.behaviors.guild

import bot.myra.diskord.common.entities.channel.VoiceChannel
import bot.myra.diskord.common.entities.guild.voice.VoiceStateData
import bot.myra.diskord.rest.behaviors.DiskordObject
import bot.myra.diskord.rest.getChannel
import bot.myra.diskord.rest.request.Result

interface VoiceStateBehavior : DiskordObject {
    val data: VoiceStateData

    suspend fun getChannel(): Result<VoiceChannel>? = data.channelId?.let { diskord.getChannel<VoiceChannel>(it) }
}