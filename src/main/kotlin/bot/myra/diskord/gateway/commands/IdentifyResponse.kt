package bot.myra.diskord.gateway.commands

import bot.myra.diskord.gateway.OpCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentifyResponse(
    val token: String,
    val intents: Int,
    val properties: Properties
) : GatewayCommand(OpCode.IDENTIFY) {

    @Serializable
    data class Properties(
        @SerialName("\$os") val os: String = "linux",
        @SerialName("\$browser") val browser: String = "chrome",
        @SerialName("\$device") val device: String = "chrome"
    )

}