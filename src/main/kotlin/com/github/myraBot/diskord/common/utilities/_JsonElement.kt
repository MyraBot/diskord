package com.github.myraBot.diskord.common.utilities

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

val JsonElement.string: String get() = this.jsonPrimitive.content