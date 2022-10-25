package bot.myra.diskord.gateway.events.types

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.gateway.events.Events
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.rest.behaviors.DefaultBehavior
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

/**
 * Superclass of every listener, used to invoke events.
 * Events can be registered by calling [Diskord.addListeners].
 */
abstract class Event : EventAction(), DefaultBehavior {

    private val eventTree = this::class.allSuperclasses + this::class

    override suspend fun handle() = call()

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
    private fun runFunctions(listener: EventListener, functions: List<KFunction<*>>): List<Deferred<*>> = functions
        .filter { function ->
            val eventTarget = function.findAnnotation<ListenTo>()?.event ?: return@filter false
            eventTarget in eventTree
        }
        .mapNotNull { runEvent(it, listener) }

    /**
     * Executes a single event function.
     * If the function has no event parameter it gets launched in a new coroutine.
     * This should be used for functions which contain huge blocking code. Otherwise,
     * the entire event queue get blocked.
     * For functions with parameters the function returns a not-nullable [Deferred]
     * value.
     *
     * @param func Event function to execute.
     * @param listener Event listener superclass.
     */
    private fun runEvent(func: KFunction<*>, listener: EventListener): Deferred<*>? = runEventSafeAsync {
        if (func.valueParameters.isEmpty()) {
            Events.coroutineScope.launch { func.callSuspend(listener) }
            null
        } else {
            Events.coroutineScope.async { func.callSuspend(listener, this@Event) }
        }
    }

    private fun runEventSafeAsync(code: () -> Deferred<*>?): Deferred<*>? {
        return try {
            code.invoke()
        } catch (e: Exception) {
            Diskord.errorHandler.onException(this@Event, e)
            null
        }
    }

}