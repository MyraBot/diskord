package com.github.myraBot.diskord.gateway.listeners

import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.rest.behaviors.DefaultBehavior
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation

abstract class Event : DefaultBehavior {

    /**
     * Calls all registered events which [ListenTo] the called event.
     */
    open suspend fun call() {
        Diskord.listeners.forEach { listener ->
            listener.functions
                .filter { it.findAnnotation<ListenTo>()?.event == this::class }
                .forEach { it.callSuspend(listener, this) }
        }
    }

}