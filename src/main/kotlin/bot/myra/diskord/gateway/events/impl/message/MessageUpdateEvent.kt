package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.message.UpdatedMessage
import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class MessageUpdateEvent(
    @SerialName("message") val updatedMessage: UpdatedMessage
) : Event() {

    override suspend fun handle() {
        if (updatedMessage.edited != null) call()
    }

}