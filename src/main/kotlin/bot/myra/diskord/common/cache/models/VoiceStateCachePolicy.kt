package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericGuildCachePolicy
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.cache.VoiceStateCacheKey
import bot.myra.diskord.common.entities.guild.voice.VoiceStateData
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.create.GenericGuildCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.voice.VoiceStateUpdateEvent
import bot.myra.diskord.gateway.events.types.Event

class MutableVoiceStateCachePolicy : VoiceStateCachePolicy() {

    private fun checkIntents(event: Event) {
        if (GatewayIntent.GUILD_VOICE_STATES !in event.diskord.intents) {
            throw MissingIntentException(VoiceStateCachePolicy::class, GatewayIntent.GUILD_VOICE_STATES)
        }
    }

    @ListenTo(GenericGuildCreateEvent::class)
    suspend fun onGuildCreate(event: GenericGuildCreateEvent) {
        checkIntents(event)
        if (event.guild.available) {
            val guild = event.guild.asExtendedGuild()
            guild.voiceStates.forEach { updateVoiceState(it) }
        }
    }

    @ListenTo(VoiceStateUpdateEvent::class)
    suspend fun onVoiceStateUpdate(event: VoiceStateUpdateEvent) {
        checkIntents(event)
        updateVoiceState(event.newVoiceState.data)
    }

    private suspend fun updateVoiceState(state: VoiceStateData) {
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

abstract class VoiceStateCachePolicy : GenericGuildCachePolicy<VoiceStateCacheKey, VoiceStateData>() {
    override fun isFromGuild(value: VoiceStateData): String? = value.guildId
    override fun getAsKey(value: VoiceStateData): VoiceStateCacheKey = VoiceStateCacheKey(value.guildId!!, value.userId) // TODO npe risiko
}