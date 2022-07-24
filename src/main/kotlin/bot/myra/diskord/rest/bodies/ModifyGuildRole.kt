package bot.myra.diskord.rest.bodies

import bot.myra.diskord.common.serializers.SColor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class ModifyGuildRole(
    var name: String? = null,
    var permissions: String? = null,
    @Serializable(with = SColor::class) @SerialName("color") var colour: Color? = null,
    var hoist: Boolean? = null
)
