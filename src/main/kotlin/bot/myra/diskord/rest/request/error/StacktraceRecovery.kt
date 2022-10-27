package bot.myra.diskord.rest.request.error

import bot.myra.diskord.rest.request.HttpRequest
import bot.myra.diskord.rest.request.RestClient

/**
 * Responsible to keep the stacktrace after it got lost through Ktor's
 * [io.ktor.util.pipeline.SuspendFunctionGun]
 */
object StacktraceRecovery {

    suspend fun <T> handle(request: HttpRequest<T>): T {
        val dummyException = RecoverableException()
        return try {
            RestClient.executeRequest(request)!!
        } catch (e: Exception) {
            throw   dummyException.apply {
                initCause(e)
            }
        }
    }

    suspend fun <T> handleNullable(request: HttpRequest<T>): T? {
        val dummyException = RecoverableException()
        return try {
            println("Executing")
            RestClient.executeRequest(request)
        } catch (e: Exception) {
            return when (e) {
                is MissingPermissionsException -> null
                is NotFoundException           -> null
                else                           -> if (request.ignoreBadRequest && e is BadReqException) return null else throw   dummyException.apply { initCause(e) }
            }
        }
    }

}