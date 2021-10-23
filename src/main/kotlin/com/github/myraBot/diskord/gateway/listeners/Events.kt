package com.github.myraBot.diskord.gateway.listeners

import com.github.m5rian.discord.DiscordBot
import com.github.m5rian.discord.JSON
import com.github.m5rian.discord.OptCode
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.gateway.listeners.impl.MessageCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.ReadyEvent
import com.github.myraBot.diskord.gateway.listeners.impl.UnknownEvent
import kotlinx.serialization.json.decodeFromJsonElement
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
            "MESSAGE_CREATE" -> MessageCreateEvent(JSON.decodeFromJsonElement<Message>(income.d!!))
            else -> JSON.decodeFromJsonElement<UnknownEvent>(income.d!!)
        }.call()
    }

    fun register() {
        DiscordBot.listeners
            .forEach { listener ->
                val eventFunctions = listener::class.declaredFunctions
                    .filter {
                        val klass = it.valueParameters.firstOrNull()?.type?.classifier ?: return@filter false
                        Event::class.isSuperclassOf(klass as KClass<*>)
                    }
                    .filter { it.hasAnnotation<ListenTo>() }
                listener.functions.addAll(eventFunctions)
            }
    }

}