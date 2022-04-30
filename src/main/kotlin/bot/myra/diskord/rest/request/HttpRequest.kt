package bot.myra.diskord.rest.request

import bot.myra.diskord.common.Arguments
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.rest.Route

data class HttpRequest<T>(
    val route: Route<T>,
    var json: String? = null,
    var logReason: String? = null,
    var attachments: List<File> = emptyList(),
    val arguments: Arguments = Arguments(),
    var ignoreBadRequest: Boolean = false
) {
    fun arguments(arguments: Arguments.() -> Unit) {
        this.arguments.apply(arguments)
    }
}