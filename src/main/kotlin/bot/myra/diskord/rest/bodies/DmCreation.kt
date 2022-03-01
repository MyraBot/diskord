package bot.myra.diskord.rest.bodies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/resources/user#create-dm-json-params)
 *
 * @property recipient The id of the recipient to open a DM channel with.
 */
@Serializable
data class DmCreation(
        @SerialName("recipient_id") val recipient: String
)
