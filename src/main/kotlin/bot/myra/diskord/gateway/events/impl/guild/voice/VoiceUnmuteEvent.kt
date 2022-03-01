package bot.myra.diskord.gateway.events.impl.guild.voice

import bot.myra.diskord.common.entities.guild.voice.VoiceState
import bot.myra.diskord.gateway.events.Event

data class VoiceUnmuteEvent(
        val newVoiceState: VoiceState
) : Event()