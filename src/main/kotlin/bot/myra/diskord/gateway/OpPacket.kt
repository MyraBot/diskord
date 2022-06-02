package bot.myra.diskord.gateway

import bot.myra.diskord.common.utilities.JSON
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class OpPacket(
    var t: String? = null,
    var s: Int? = null,
    var op: Int,
    var d: JsonElement? = null
) {
    constructor(t: String?, s: Int?, op: Int, d: String)
            : this(t, s, op, JSON.encodeToJsonElement(d))

    constructor(t: String?, s: Int?, op: Int, d: Int)
            : this(t, s, op, JSON.encodeToJsonElement(d))

    constructor(t: String?, s: Int?, op: Int, d: JsonObject)
            : this(t, s, op, JSON.encodeToJsonElement(d))
}