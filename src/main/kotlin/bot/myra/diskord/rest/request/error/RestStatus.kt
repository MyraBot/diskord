package bot.myra.diskord.rest.request.error

import kotlin.reflect.full.primaryConstructor


sealed class RestStatus(
    val status: Int,
    val success: Boolean,
    val message: String
) {

    companion object {
        fun getByStatusCode(status: Int): RestStatus =
            RestStatus::class.sealedSubclasses
                .asSequence()
                .filter { it != UnknownError::class }
                .mapNotNull { it.primaryConstructor?.call() }
                .find { it.status == status }
                ?: UnknownError(status)
    }

    class Ok : RestStatus(200, true, "The request completed successfully.")
    class Created : RestStatus(201, true, "The entity was created successfully.")
    class NoContent : RestStatus(204, true, "The request completed successfully but returned no content.")
    class NotModified : RestStatus(304, true, "The entity was not modified (no action was taken).")

    class BadRequest : RestStatus(400, false, "The request was improperly formatted, or the server couldn't understand it.")
    class Unauthorized : RestStatus(401, false, "The Authorization header was missing or invalid.")
    class MissingPermissions : RestStatus(403, false, "The Authorization token you passed did not have permission to the resource.")
    class NotFound : RestStatus(404, false, "The resource at the location specified doesn't exist.")
    class MethodNotAllowed : RestStatus(405, false, "The HTTP method used is not valid for the location specified.")
    class TooManyRequests : RestStatus(429, false, "You are being rate limited.")
    class GatewayUnavailable : RestStatus(502, false, "There was not a gateway available to process your request. Wait a bit and retry.")
    class UnknownError(status: Int) : RestStatus(status, false, "An unknown error occurred")
}