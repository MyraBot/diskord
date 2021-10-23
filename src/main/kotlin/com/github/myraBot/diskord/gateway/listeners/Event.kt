package com.github.myraBot.diskord.gateway.listeners

import com.github.m5rian.discord.DiscordBot
import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.rest.Endpoints
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation

abstract class Event {
    suspend fun bot(): Application = Endpoints.getBotApplication.execute()

    /**
     * Calls all registered events which [ListenTo] the called event.
     */
    suspend fun call() {
        DiscordBot.listeners.forEach { listener ->
            listener.functions
                .filter { it.findAnnotation<ListenTo>()?.event == this::class }
                .forEach { it.callSuspend(listener, this) }
        }
    }

}