package com.github.myraBot.diskord.common

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

inline fun <reified T> T.toJson() = JSON.encodeToString(this)