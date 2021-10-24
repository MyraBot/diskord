package com.github.myraBot.diskord.gateway.listeners.impl

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadyEvent(
        @SerialName("v") val version: Int,
        val user: UserData,
        @SerialName("session_id") val sessionId: String
) : Event()