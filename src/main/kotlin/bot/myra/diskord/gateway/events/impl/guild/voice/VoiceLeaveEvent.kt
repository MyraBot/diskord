package bot.myra.diskord.gateway.events.impl.guild.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.VoiceChannel
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.events.types.Event
import bot.myra.diskord.rest.request.Result
import kotlinx.coroutines.runBlocking

data class VoiceLeaveEvent(
    private val newVoiceState: VoiceState,
    private val oldVoiceState: VoiceState,
    override val diskord: Diskord
) : Event() {
    fun getMember(): Member = oldVoiceState.member!!
    val channel: Result<VoiceChannel> get() = runBlocking { oldVoiceState.getChannel()!! }
}