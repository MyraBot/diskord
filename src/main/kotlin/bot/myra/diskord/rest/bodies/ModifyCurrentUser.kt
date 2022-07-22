package bot.myra.diskord.rest.bodies

import kotlinx.serialization.Serializable

@Serializable
data class ModifyCurrentUser(
    val username: String,
    val avatar: String?
)
