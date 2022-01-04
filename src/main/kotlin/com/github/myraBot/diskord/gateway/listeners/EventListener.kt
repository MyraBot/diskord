package com.github.myraBot.diskord.gateway.listeners

import com.github.myraBot.diskord.common.Diskord
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.valueParameters

abstract class EventListener {
    val functions = mutableListOf<KFunction<*>>()

    /**
     * Loads all functions of this listener in its [EventListener.functions] value.
     */
    fun loadAsCache() {
        this::class.declaredFunctions
            .filter { it.hasAnnotation<ListenTo>() }
            .filter {
                // Get the first parameter of the function, if the function has no parameter add it still to the listeners,
                // to execute also listeners with no parameters.
                val klass = it.valueParameters.firstOrNull()?.type?.classifier ?: return@filter true
                Event::class.isSuperclassOf(klass as KClass<*>)
            }.let {
                this.functions.addAll(it) // Load all functions in the listener
            }
    }

    /**
     * Loads all functions of this listener in its [EventListener.functions] value.
     * Puts the current listener in [Diskord.listeners] to call on events.
     */
    fun loadAsListener() {
        this::class.declaredFunctions
            .filter { it.hasAnnotation<ListenTo>() }
            .filter {
                // Get the first parameter of the function, if the function has no parameter add it still to the listeners,
                // to execute also listeners with no parameters.
                val klass = it.valueParameters.firstOrNull()?.type?.classifier ?: return@filter true
                Event::class.isSuperclassOf(klass as KClass<*>)
            }.let {
                this.functions.addAll(it) // Load all functions in the listener
                Diskord.listeners.add(this) // Add listener with functions to the registered listeners
            }
    }

}