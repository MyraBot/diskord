package bot.myra.diskord.rest.bodies

import kotlinx.serialization.SerialName

/**
 * @property deleteMessageDays The number of days to delete messages for (0-7).
 */
@kotlinx.serialization.Serializable
data class BanInfo(
    @SerialName("delete_message_days") val deleteMessageDays: Int? = null
)
