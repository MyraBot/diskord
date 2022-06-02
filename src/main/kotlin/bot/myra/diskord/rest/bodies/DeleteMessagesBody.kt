package bot.myra.diskord.rest.bodies

import kotlinx.serialization.Serializable

@Serializable
data class DeleteMessagesBody(
    val messages:List<String>
)
