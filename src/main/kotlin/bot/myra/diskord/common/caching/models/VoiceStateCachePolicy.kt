package bot.myra.diskord.common.caching.models

import bot.myra.diskord.common.caching.DoubleKey
import bot.myra.diskord.common.caching.GenericCachePolicy
import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GenericGuildCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.voice.VoiceStateUpdateEvent

class VoiceStateCachePolicy : GenericCachePolicy<DoubleKey<String?, String>, VoiceState>() {

    @ListenTo(GenericGuildCreateEvent::class)
    fun onGuildCreate(event: GenericGuildCreateEvent) = event.guild.voiceStates.forEach { updateVoiceState(it) }

    @ListenTo(VoiceStateUpdateEvent::class)
    fun onVoiceStateUpdate(event: VoiceStateUpdateEvent) = updateVoiceState(event.newVoiceState)

    private fun updateVoiceState(state: VoiceState) {
        // Add voice state
        if (state.channelId !== null) {
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