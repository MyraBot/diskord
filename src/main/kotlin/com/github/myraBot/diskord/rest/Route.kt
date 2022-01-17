package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.Arguments
import io.ktor.http.*
import kotlinx.serialization.KSerializer

data class Route<R>(
        val httpMethod: HttpMethod,
        val path: String,
        val serializer: KSerializer<R>,
        val cache: ((R, Arguments) -> Unit)? = null,
)