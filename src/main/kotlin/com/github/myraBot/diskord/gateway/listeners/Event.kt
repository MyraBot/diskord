package com.github.myraBot.diskord.gateway.listeners

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.rest.behaviors.DefaultBehavior
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ForkJoinPool
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

private val scope: CoroutineScope = CoroutineScope(ForkJoinPool().asCoroutineDispatcher())

abstract class Event : DefaultBehavior {

    private suspend fun runCacheUpdating() = Diskord.cache.forEach { runFunctions(it.cache) }

    /**
     * Calls all registered events which [ListenTo] the called event.
     */
    open suspend fun call() = runCacheUpdating().also { Diskord.listeners.forEach { runFunctions(it) } }

    private suspend fun runFunctions(listener: EventListener) = listener.functions
        .filter { it.findAnnotation<ListenTo>()?.event == this::class }
        .forEach { scope.launch() { runEvent(it, listener) } }

    private suspend fun runEvent(func: KFunction<*>, listener: EventListener) {
        try {
            if (func.valueParameters.isEmpty()) func.callSuspend(listener)
            else func.callSuspend(listener, this@Event)
        } catch (e: InvocationTargetException) {
            Diskord.errorHandler.onException(this, e.targetException)
        }
    }

}