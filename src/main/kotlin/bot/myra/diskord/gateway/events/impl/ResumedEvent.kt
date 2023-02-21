package bot.myra.diskord.gateway.events.impl

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.types.Event

class ResumedEvent(
    override val diskord: Diskord
) : Event() {

    fun deserialize(diskord: Diskord): ResumedEvent {
        return ResumedEvent(diskord)
    }

    override suspend fun handle() {
        diskord.gateway.logger.info("Resumed connection!")
        call()
    }
}