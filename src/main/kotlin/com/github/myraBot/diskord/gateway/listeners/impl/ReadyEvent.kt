package com.github.myraBot.diskord.gateway.listeners.impl

import com.github.m5rian.discord.objects.entities.User
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadyEvent(
        @SerialName("v") val version: Int,
        val user: User,
        @SerialName("session_id") val sessionId: String
) : Event()