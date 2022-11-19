package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.Serializable

@Serializable
class ResumedEvent : Event() {
    override suspend fun handle() {
        Diskord.gateway.logger.info("Resumed connection!")
        call()
    }
}