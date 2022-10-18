package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.types.Event
import bot.myra.kommons.info
import kotlinx.serialization.Serializable

@Serializable
class ResumedEvent : Event() {
    override suspend fun handle() {
        info(Diskord.gateway::class) { "Resumed connection!" }
        call()
    }
}