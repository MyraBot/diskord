package bot.myra.diskord.common.utilities

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

internal val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}
internal val JSON_WITH_NULLS = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = true
}

internal inline fun <reified T> T.toJson(explicitNulls: Boolean = false): String {
    return if (explicitNulls) JSON_WITH_NULLS.encodeToString(this)
    else JSON.encodeToString(this)
}

internal inline fun <reified T> T.toJsonObj(explicitNulls: Boolean = false): JsonObject {
    return if (explicitNulls) JSON_WITH_NULLS.encodeToJsonElement(this).jsonObject
    else JSON.encodeToJsonElement(this).jsonObject
}