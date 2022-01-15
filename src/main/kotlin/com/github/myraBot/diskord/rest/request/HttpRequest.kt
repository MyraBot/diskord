package com.github.myraBot.diskord.rest.request

import com.github.myraBot.diskord.common.Arguments
import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.rest.Route

data class HttpRequest<T>(
        val route: Route<T>,
        val json: String?,
        val files: List<File>,
        val arguments: Arguments,
)