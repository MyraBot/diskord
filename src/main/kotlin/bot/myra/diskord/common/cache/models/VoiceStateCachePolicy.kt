package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GenericGuildCreateEvent
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
        // Add voice state
        if (state.channelId != null) {
            // Remove outdated voice states ➜ important if user got moved to a different channel
            view().forEach { oldState ->
                // Found old voice state
                if (oldState.userId == state.userId) {
                    val key = DoubleKey(oldState.guildId, oldState.userId)
                    remove(key)
                }
            }

            update(state)
        }
        // Remove voice state
        else {
            val key = DoubleKey(state.guildId, state.userId)
            remove(key)
        }
    }

}

class DisabledVoiceStateCachePolicy : VoiceStateCachePolicy()

abstract class VoiceStateCachePolicy : GenericCachePolicy<DoubleKey<String?, String>, VoiceState>()