package bot.myra.diskord.rest.request

import bot.myra.diskord.common.Arguments
import bot.myra.diskord.common.entities.File
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.Route

data class HttpRequest<T>(
    val route: Route<T>,
    val queryParameter: MutableList<Pair<String, Any>> = mutableListOf(),
    var json: String? = null,
    var logReason: String? = null,
    var attachments: List<File> = emptyList(),
    val arguments: Arguments = Arguments(),
    var ignoreBadRequest: Boolean = false,
) {
    fun arguments(arguments: Arguments.() -> Unit) {
        this.arguments.apply(arguments)
    }


    fun getFullPath(): String {
        var route = Endpoints.baseUrl + route.path
        arguments.entries.forEach { route = route.replace("{${it.key}}", it.value.toString()) }
        return route
    }

}