package bot.myra.diskord.gateway.events.impl.guild.voice

import bot.myra.diskord.common.entities.channel.VoiceChannel
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

data class VoiceJoinEvent(val voiceState: VoiceState) : Event() {
    fun getChannelAsync(): Deferred<VoiceChannel?> = voiceState.getChannelAsync()
}
