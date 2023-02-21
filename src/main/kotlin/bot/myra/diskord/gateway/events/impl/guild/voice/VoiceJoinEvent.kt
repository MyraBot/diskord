package bot.myra.diskord.gateway.events.impl.guild.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.events.types.Event

data class VoiceJoinEvent(
    val voiceState: VoiceState,
    override val diskord: Diskord
) : Event() {
    suspend fun getChannel() = voiceState.getChannel()
}
