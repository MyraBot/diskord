package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.types.Event

data class InitialReadyEvent(
    val event: ReadyEvent,
    override val diskord: Diskord
) : Event()