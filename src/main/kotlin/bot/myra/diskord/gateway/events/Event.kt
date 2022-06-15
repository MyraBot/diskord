package bot.myra.diskord.gateway.events

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.behaviors.DefaultBehavior
import kotlinx.coroutines.launch
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

    open fun prepareEvent() {}

    /**
     * Prepares the event to be ready to be called and executes the events for all registered listeners.
     */
    fun call() {
        Events.coroutineScope.launch {
            prepareEvent()
            Diskord.listeners.forEach { (klass, functions) -> runFunctions(klass, functions) }
        }
    }

    /**
     * Calls [runEvent] for all registered functions matching the event.
     *
     * @param listener Event listener superclass.
     * @param functions Already pre-filtered listener functions.
     */
    private fun runFunctions(listener: EventListener, functions: List<KFunction<*>>) {
        functions.filter { function ->
            val eventTarget = function.findAnnotation<ListenTo>()?.event ?: return@filter false
            eventTarget in this::class.superclasses
        }.forEach { Events.coroutineScope.launch { runEvent(it, listener) } }
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