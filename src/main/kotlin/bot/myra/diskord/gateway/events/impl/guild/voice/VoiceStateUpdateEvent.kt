package bot.myra.diskord.gateway.events.impl.guild.voice

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.common.entities.guild.voice.VoiceStateData
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#voice-state-update)
 *
 * Sent when someone joins/leaves/moves voice channels.
 *
 * @property newVoiceState Information about the current voice state of the member.
 */
data class VoiceStateUpdateEvent(
    val newVoiceState: VoiceState,
    val oldVoiceState: VoiceState?,
    override val diskord: Diskord
) : Event() {

    companion object {
        suspend fun deserialize(json: JsonElement, decoder: Json, diskord: Diskord): VoiceStateUpdateEvent {
            val data = decoder.decodeFromJsonElement<VoiceStateData>(json)
            val newVoiceState = VoiceState(data, diskord)
            val oldVoiceState = diskord.cachePolicy.voiceState.view()
                .find { it.userId == newVoiceState.userId && it.guildId == newVoiceState.guildId }
                ?.let { VoiceState(it, diskord) }
            return VoiceStateUpdateEvent(newVoiceState, oldVoiceState, diskord)
        }
    }

    override suspend fun handle() {
        if (oldVoiceState == null && newVoiceState.channelId != null) VoiceJoinEvent(newVoiceState, diskord).handle()
        else if (oldVoiceState?.channelId != null && newVoiceState.channelId == null) VoiceLeaveEvent(newVoiceState, oldVoiceState, diskord).handle()
        else if (oldVoiceState != null && oldVoiceState.channelId != newVoiceState.channelId) VoiceMoveEvent(oldVoiceState, newVoiceState, diskord).handle()

        if (oldVoiceState?.isMuted == false && newVoiceState.isMuted) VoiceMuteEvent(newVoiceState, diskord).handle()
        else if (oldVoiceState?.isMuted == true && !newVoiceState.isMuted) VoiceUnmuteEvent(newVoiceState, diskord).handle()

        if (oldVoiceState?.isDeaf == false && newVoiceState.isDeaf) VoiceDeafEvent(newVoiceState, diskord).handle()
        else if (oldVoiceState?.isDeaf == true && !newVoiceState.isDeaf) VoiceUndeafEvent(newVoiceState, diskord).handle()
        call()
    }

    val member get() = newVoiceState.member
    suspend fun getGuild() = newVoiceState.guildId?.let { diskord.getGuild(it) }
}
