package bot.myra.diskord.rest.request

import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.rest.request.error.BadReqException
import bot.myra.diskord.rest.request.error.MissingPermissionsException
import bot.myra.diskord.rest.request.error.NotFoundException
import bot.myra.diskord.rest.request.error.RestStatus
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement

data class Result<T>(
    val value: T? = null,
    internal val statusRaw: HttpStatusCode,
    val error: JsonElement?
) {
    val status: RestStatus = RestStatus.getByStatusCode(statusRaw.value)

    fun getOrThrow(): T {
        if (status.success) return value!!

        throw when (status) {
            is RestStatus.BadRequest         -> BadReqException(status.message)
            is RestStatus.Unauthorized       -> Exception(status.message) // Internal exception
            is RestStatus.MissingPermissions -> MissingPermissionsException(status.message)
            is RestStatus.NotFound           -> NotFoundException(status.message)
            is RestStatus.MethodNotAllowed   -> Exception(status.message) // Internal exception
            is RestStatus.GatewayUnavailable -> Exception(status.message)
            else                             -> {
                val jsonString = error?.let { JSON.encodeToString(it) }
                Exception("Unknown error: ${statusRaw.description} (${statusRaw.value}) - $jsonString")
            }
        }
    }

    fun <R> transformValue(function: (T) -> R): Result<R> {
        return Result(value?.let(function), statusRaw, error)
    }

    suspend fun orTry(result: suspend () -> Result<T>): Result<T> {
        return if (status.success) this
        else result.invoke()
    }

}

fun <T, R> Result<List<T>>.transformEach(transform: (T) -> R): Result<List<R>> {
    return Result(value?.map(transform), statusRaw, error)
}