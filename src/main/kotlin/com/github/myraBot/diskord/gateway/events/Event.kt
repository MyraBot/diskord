package com.github.myraBot.diskord.gateway.events

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.rest.behaviors.DefaultBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ForkJoinPool
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

private val scope: CoroutineScope = CoroutineScope(ForkJoinPool().asCoroutineDispatcher())

/**
 * Superclass of every listener, used to invoke events.
 * Events can be registered by calling [Diskord.addListeners].
 */
abstract class Event : DefaultBehavior {

    /**
     * Runs [runFunctions] for caching purpose and
     * for all registered listeners.
     */
    open suspend fun call() {
        Diskord.cache.forEach { runFunctions(it.listener, it.listener::class.declaredFunctions.toList()) } //TODO
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
     * @param listener Event listener superclass.
     * @param functions Fully filtered listener functions.
     */
    private suspend fun runEvent(func: KFunction<*>, listener: EventListener) {
        try {
            if (func.valueParameters.isEmpty()) func.callSuspend(listener)
            else func.callSuspend(listener, this@Event)
        } catch (e: InvocationTargetException) {
            Diskord.errorHandler.onException(this, e.targetException)
        }
    }

}