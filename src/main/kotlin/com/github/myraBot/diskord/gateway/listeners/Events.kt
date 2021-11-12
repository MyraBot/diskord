package com.github.myraBot.diskord.gateway.listeners

import com.github.m5rian.discord.OptCode
import com.github.m5rian.discord.info
import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.gateway.Websocket
import com.github.myraBot.diskord.gateway.listeners.impl.message.MessageCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.ReadyEvent
import com.github.myraBot.diskord.gateway.listeners.impl.UnknownEvent
import com.github.myraBot.diskord.gateway.listeners.impl.interactions.InteractionCreateEvent
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.json.decodeFromJsonElement
import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.valueParameters

object Events {

    suspend fun resolve(income: OptCode) {
        println(income.toJson())

        val data = income.d!!
        when (income.t) {
            "READY" -> JSON.decodeFromJsonElement<ReadyEvent>(data).also { Websocket.session = it.sessionId }
            "MESSAGE_CREATE" -> MessageCreateEvent(JSON.decodeFromJsonElement(data))
            "INTERACTION_CREATE" -> InteractionCreateEvent(JSON.decodeFromJsonElement(data))
            else -> JSON.decodeFromJsonElement<UnknownEvent>(data)
        }.call()
    }

    fun register(packageName: String, listeners: MutableList<EventListener>) {
        info(this::class) { "Registering discord event listeners" }

        // Load custom registered listeners
        listeners.forEach { listener -> loadListener(listener) }.also {

            // Load listeners by reflection
            if (packageName.isNotBlank()) {
                Reflections(packageName).getSubTypesOf(EventListener::class.java)
                    .map { it.kotlin.objectInstance }
                    .forEach { listener ->
                        if (listener == null) throw IllegalStateException("Make sure all listeners are objects!")
                        loadListener(listener)
                    }
            }

        }

    }

    private fun loadListener(listener: EventListener) {
        listener::class.declaredFunctions
            .filter { it.hasAnnotation<ListenTo>() }
            .filter {
                val klass = it.valueParameters.firstOrNull()?.type?.classifier ?: false
                Event::class.isSuperclassOf(klass as KClass<*>)
            }.let {
                listener.functions.addAll(it) // Load all functions in the listener
                Diskord.listeners.add(listener) // Add listener with functions to the registered listeners
            }
    }

}