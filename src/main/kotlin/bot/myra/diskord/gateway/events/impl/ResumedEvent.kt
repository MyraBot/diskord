package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.Event
import bot.myra.kommons.info
import kotlinx.serialization.Serializable

@Serializable
class ResumedEvent : Event() {
    override fun prepareEvent() {
        info(Diskord.gateway::class) { "Resumed connection!" }
    }
}