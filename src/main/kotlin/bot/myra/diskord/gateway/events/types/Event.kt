package bot.myra.diskord.gateway.events.types

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.gateway.events.Events
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.rest.behaviors.DefaultBehavior
import bot.myra.diskord.rest.behaviors.DiskordObject
import kotlinx.coroutines.CoroutineScope
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
abstract class Event : EventAction(), DefaultBehavior, DiskordObject {
    val eventTree = this::class.allSuperclasses + this::class

    override suspend fun handle() = call()
    /**
     * Executes the events for all registered listeners.
     */
    fun call() {
        diskord.cachePolicy.all().forEach { cache -> runFunctions(cache, cache.eventFunctions) }
        diskord.listeners.forEach { runFunctions(it.key, it.value) }
    }

    /**
     * Calls [runEvent] for all registered functions matching the event.
     *
     * @param listener Event listener superclass.
     * @param functions Already pre-filtered listener functions.
     */
    private fun runFunctions(listener: EventListener, functions: List<KFunction<*>>) = functions
        .filter { function ->
            val eventTarget = function.findAnnotation<ListenTo>()?.event ?: return@filter false
            eventTarget in eventTree
        }
        .forEach { runEvent(it, listener) }

    /**
     * Executes a single event function.
     *
     * @param func Event function to execute.
     * @param listener Event listener superclass.
     */
    private fun runEvent(func: KFunction<*>, listener: EventListener) {
        fun launchCatching(block: suspend CoroutineScope.() -> Unit) {
            Events.coroutineScope.launch {
                try {
                    block.invoke(this)
                } catch (e: Exception) {
                    diskord.errorHandler.onException(this@Event, e.cause ?: e)
                }
            }
        }

        return if (func.valueParameters.isEmpty()) launchCatching { func.callSuspend(listener) }
        else launchCatching { func.callSuspend(listener, this@Event) }
    }

}