package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
data class InitialReadyEvent(val event: ReadyEvent) : Event()