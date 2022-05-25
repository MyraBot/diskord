package bot.myra.diskord.gateway.handler

import bot.myra.diskord.common.utilities.JSON
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class Opcode(
    val t: String?,
    val s: Int?,
    val op: Int,
    val d: JsonElement? = null
) {
    constructor(t: String?, s: Int?, op: Int, d: String)
            : this(t, s, op, JSON.encodeToJsonElement(d))

    constructor(t: String?, s: Int?, op: Int, d: Int)
            : this(t, s, op, JSON.encodeToJsonElement(d))

    constructor(t: String?, s: Int?, op: Int, d: JsonObject)
            : this(t, s, op, JSON.encodeToJsonElement(d))
}

@Serializable
data class IdentifyResponse(
    val token: String,
    val intents: Int,
    val properties: Properties
)

@Serializable
data class Properties(
    @SerialName("\$os") val os: String = "linux",
    @SerialName("\$browser") val browser: String = "chrome",
    @SerialName("\$device") val device: String = "chrome"
)

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#resume-resume-structure)
 * Gets send when the websocket connection needs to be resumed.
 */
@Serializable
data class GatewayResume(
    val token: String,
    @SerialName("session_id") val sessionId: String,
    val seq: Int
)