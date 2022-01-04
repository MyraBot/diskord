package com.github.myraBot.diskord.gateway.listeners

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.rest.behaviors.DefaultBehavior
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

abstract class Event : DefaultBehavior {

    private suspend fun runCacheUpdating() = Diskord.cache.forEach { runFunctions(it.cache) }

    /**
     * Calls all registered events which [ListenTo] the called event.
     */
    open suspend fun call() = runCacheUpdating().also { Diskord.listeners.forEach { runFunctions(it) } }

    private suspend fun runFunctions(listener: EventListener) = listener.functions
        .filter { it.findAnnotation<ListenTo>()?.event == this::class }
        .forEach {
            if (it.valueParameters.isEmpty()) it.callSuspend(listener)
            else it.callSuspend(listener, this)
        }
}