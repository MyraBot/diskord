package bot.myra.diskord.rest.request

import bot.myra.diskord.rest.request.error.BadReqException
import bot.myra.diskord.rest.request.error.MissingPermissionsException
import bot.myra.diskord.rest.request.error.ModificationException
import bot.myra.diskord.rest.request.error.NotFoundException
import io.ktor.http.*

data class Result<T>(
    val value: T? = null,
    val status: HttpStatusCode,
    val errorMessage: String?
) {

    fun getOrThrow(): T {
        if (status.isSuccess()) return value!!

        val message = errorMessage ?: "No error message provided"
        throw when (status) {
            HttpStatusCode.NotModified      -> ModificationException(message)
            HttpStatusCode.BadRequest       -> BadReqException(message)
            HttpStatusCode.Unauthorized     -> Exception("Unauthorized") // Internal exception
            HttpStatusCode.Forbidden        -> MissingPermissionsException(message)
            HttpStatusCode.NotFound         -> NotFoundException(message)
            HttpStatusCode.MethodNotAllowed -> Exception("Method not allowed") // Internal exception
            else                            -> Exception("Unknown error: ${status.description} (${status.value}) - $message")
        }
    }

    fun <R> transformValue(function: (T) -> R): Result<R> {
        return Result(value?.let(function), status, errorMessage)
    }

    suspend fun orTry(result: suspend () -> Result<T>): Result<T> {
        val res = result.invoke()
        return if (res.status.isSuccess()) res
        else this
    }

}