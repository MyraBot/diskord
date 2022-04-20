package bot.myra.diskord.gateway.events.impl.guild.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.events.Event
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
) : Event() {

    override suspend fun prepareEvent() {
        if (oldVoiceState == null && newVoiceState.channelId != null) VoiceJoinEvent(newVoiceState)
        else if (oldVoiceState?.channelId != null && newVoiceState.channelId == null) VoiceLeaveEvent(newVoiceState, oldVoiceState)
        else if (oldVoiceState != null && oldVoiceState.channelId != newVoiceState.channelId) VoiceMoveEvent(oldVoiceState, newVoiceState)

        if (oldVoiceState?.isMuted == false && newVoiceState.isMuted) VoiceMuteEvent(newVoiceState)
        if (oldVoiceState?.isMuted == true && !newVoiceState.isMuted) VoiceUnmuteEvent(newVoiceState)

        if (oldVoiceState?.isDeaf == false && newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState)
        if (oldVoiceState?.isDeaf == true && !newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState)
    }

    fun getMember(): Member? = newVoiceState.getMember()
    suspend fun getGuild():Guild? = newVoiceState.guildId?.let { Diskord.getGuild(it) }
    val oldVoiceState: VoiceState? = Diskord.cachePolicy.voiceStateCache.view().find {  it.userId == newVoiceState.userId && it.guildId == newVoiceState.guildId}

}
