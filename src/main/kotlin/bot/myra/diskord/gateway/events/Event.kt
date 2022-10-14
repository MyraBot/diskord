package bot.myra.diskord.gateway.events

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.behaviors.DefaultBehavior
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.valueParameters

/**
 * Superclass of every listener, used to invoke events.
 * Events can be registered by calling [Diskord.addListeners].
 */
abstract class Event : DefaultBehavior {

    open suspend fun handle() = call()

    /**
     * Executes the events for all registered listeners.
     * The caching listeners get executed after the custom listeners,
     * so that we are able to access the old values of the event there.
     */
    suspend fun call() {
        // Execute users listeners first
        Diskord.listeners.flatMap { runFunctions(it.key, it.value) }.awaitAll()
        // Run caching listeners after this
        Diskord.cachePolicy.all().flatMap { cache -> runFunctions(cache, cache.eventFunctions) }.awaitAll()
    }

    /**
     * Calls [runEvent] for all registered functions matching the event.
     *
     * @param listener Event listener superclass.
     * @param functions Already pre-filtered listener functions.
     */
    private fun runFunctions(listener: EventListener, functions: List<KFunction<*>>): List<Deferred<Unit>> {
        // Get all events which are triggered (including subtypes)
        val triggeredEvents = this::class.superclasses.toMutableList()
            .filter { !it.java.isInterface }
            .toMutableList()
            .apply { add(this::class) }

        return functions
            .filter { function ->
                val eventTarget = function.findAnnotation<ListenTo>()?.event ?: return@filter false
                eventTarget in triggeredEvents
            }
            .map { Events.coroutineScope.async { runEvent(it, listener) } }
    }

    /**
     * Executes the actual functions.
     *
     * @param func Event function to execute.
     * @param listener Event listener superclass.
     */
    private suspend fun runEvent(func: KFunction<*>, listener: EventListener) {
        try {
            if (func.valueParameters.isEmpty()) func.callSuspend(listener)
            else func.callSuspend(listener, this@Event)
        } catch (e: Exception) {
            Diskord.errorHandler.onException(this, e)
        }
    }

}