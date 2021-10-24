package com.github.myraBot.diskord.gateway.listeners

import com.github.m5rian.discord.DiscordBot
import com.github.m5rian.discord.JSON
import com.github.m5rian.discord.OptCode
import com.github.m5rian.discord.info
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.gateway.listeners.impl.MessageCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.ReadyEvent
import com.github.myraBot.diskord.gateway.listeners.impl.UnknownEvent
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

        when (income.t) {
            "READY" -> JSON.decodeFromJsonElement<ReadyEvent>(income.d!!)
            "MESSAGE_CREATE" -> MessageCreateEvent(Message(JSON.decodeFromJsonElement(income.d!!)))
            else -> JSON.decodeFromJsonElement<UnknownEvent>(income.d!!)
        }.call()
    }

    fun register() {
        info(this::class) { "Registering discord event listeners" }

        // Load custom registered listeners
        DiscordBot.listeners.forEach { listener -> loadListener(listener) }.also {

            // Load listeners by reflection
            if (DiscordBot.listenerPackage.isNotBlank()) {
                Reflections(DiscordBot.listenerPackage).getSubTypesOf(EventListener::class.java)
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
                DiscordBot.listeners.add(listener) // Add listener with functions to the registered listeners
            }
    }

}