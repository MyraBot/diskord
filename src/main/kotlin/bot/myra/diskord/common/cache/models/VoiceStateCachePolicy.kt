package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.GenericGuildCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.cache.VoiceStateCacheKey
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.create.GenericGuildCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.voice.VoiceStateUpdateEvent

class MutableVoiceStateCachePolicy : VoiceStateCachePolicy() {

    init {
        if (GatewayIntent.GUILD_VOICE_STATES !in Diskord.intents) throw MissingIntentException(VoiceStateCachePolicy::class, GatewayIntent.GUILD_VOICE_STATES)
    }

    @ListenTo(GenericGuildCreateEvent::class)
    suspend fun onGuildCreate(event: GenericGuildCreateEvent) = event.guild.voiceStates.forEach { updateVoiceState(it) }

    @ListenTo(VoiceStateUpdateEvent::class)
    suspend fun onVoiceStateUpdate(event: VoiceStateUpdateEvent) = updateVoiceState(event.newVoiceState)

    private suspend fun updateVoiceState(state: VoiceState) {
        val guildId = state.guildId ?: return // Don't want to track non guild voice states

        // Add voice state
        if (state.channelId != null) {
            // Remove outdated voice states ➜ important if user got moved to a different channel
            view().forEach { oldState ->
                // Found old voice state
                if (oldState.userId == state.userId) remove(getAsKey(state), guildId)
            }

            update(state)
        }
        // Remove voice state
        else remove(getAsKey(state))
    }

}

class DisabledVoiceStateCachePolicy : VoiceStateCachePolicy()

abstract class VoiceStateCachePolicy : GenericGuildCachePolicy<VoiceStateCacheKey, VoiceState>() {
    override fun isFromGuild(value: VoiceState): String? = value.guildId
    override fun getAsKey(value: VoiceState): VoiceStateCacheKey = VoiceStateCacheKey(value.guildId!!, value.userId) // TODO npe risiko
}