package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.voice.VoiceChannelBehavior

data class VoiceChannel(
    override val data: ChannelData,
) : VoiceChannelBehavior, GuildChannel {

    fun getMembers(): List<Member> {
        val voiceStates = Diskord.cachePolicy.voiceStateCache.view().filter { it.channelId === data.id }
        return voiceStates.mapNotNull { it.getMember() }
    }

}