package bot.myra.diskord.gateway.events

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.behaviors.DefaultBehavior
import bot.myra.diskord.rest.request.error.DiscordRestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.ForkJoinPool
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

private val scope: CoroutineScope = CoroutineScope(ForkJoinPool().asCoroutineDispatcher())

/**
 * Superclass of every listener, used to invoke events.
 * Events can be registered by calling [Diskord.addListeners].
 */
abstract class Event : DefaultBehavior {

    init {
        Events.coroutineScope.launch {
            prepareEvent()
            call()
        }
    }

    open suspend fun prepareEvent() {}

    /**
     * Runs [runFunctions] for caching purpose and for all registered listeners.
     */
    private suspend fun call() {
        Diskord.listeners.forEach { (klass, functions) -> runFunctions(klass, functions) }
    }

    /**
     * Calls [runEvent] for all registered functions matching the event.
     *
     * @param listener Event listener superclass.
     * @param functions Already pre-filtered listener functions.
     */
    private suspend fun runFunctions(listener: EventListener, functions: List<KFunction<*>>) {
        functions.filter { it.findAnnotation<ListenTo>()?.event == this::class }
            .forEach { scope.launch { runEvent(it, listener) } }
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
        } catch (e: DiscordRestException) {
            Diskord.errorHandler.onException(this, e)
        }
    }

}