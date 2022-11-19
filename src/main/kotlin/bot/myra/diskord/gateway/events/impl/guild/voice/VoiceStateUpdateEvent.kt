package bot.myra.diskord.gateway.events.impl.guild.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#voice-state-update)
 *
 * Sent when someone joins/leaves/moves voice channels.
 *
 * @property newVoiceState Information about the current voice state of the member.
 */
@Suppress("MemberVisibilityCanBePrivate")
data class VoiceStateUpdateEvent(
    @SerialName("voice_state") val newVoiceState: VoiceState,
    val oldVoiceState: VoiceState? = runBlocking {
        Diskord.cachePolicy.voiceState.view().find { it.userId == newVoiceState.userId && it.guildId == newVoiceState.guildId }
    }
) : Event() {

    override suspend fun handle() {
        if (oldVoiceState == null && newVoiceState.channelId != null) VoiceJoinEvent(newVoiceState).handle()
        else if (oldVoiceState?.channelId != null && newVoiceState.channelId == null) VoiceLeaveEvent(newVoiceState, oldVoiceState).handle()
        else if (oldVoiceState != null && oldVoiceState.channelId != newVoiceState.channelId) VoiceMoveEvent(oldVoiceState, newVoiceState).handle()

        if (oldVoiceState?.isMuted == false && newVoiceState.isMuted) VoiceMuteEvent(newVoiceState).handle()
        else if (oldVoiceState?.isMuted == true && !newVoiceState.isMuted) VoiceUnmuteEvent(newVoiceState).handle()

        if (oldVoiceState?.isDeaf == false && newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState).handle()
        else if (oldVoiceState?.isDeaf == true && !newVoiceState.isDeaf) VoiceUndeafEvent(newVoiceState).handle()
        call()
    }

    fun getMember(): Member? = newVoiceState.getMember()
    suspend fun getGuild() = newVoiceState.guildId?.let { Diskord.getGuild(it) }
}
