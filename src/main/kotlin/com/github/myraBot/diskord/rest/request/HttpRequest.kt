package com.github.myraBot.diskord.rest.request

import com.github.myraBot.diskord.common.Arguments
import com.github.myraBot.diskord.common.entities.File
import com.github.myraBot.diskord.rest.Route

data class HttpRequest<T>(
    val route: Route<T>,
    var json: String? = null,
    var logReason: String? = null,
    var attachments: List<File> = emptyList(),
    val arguments: Arguments = Arguments()
) {
    fun arguments(arguments: Arguments.() -> Unit) {
        this.arguments.apply(arguments)
    }
}