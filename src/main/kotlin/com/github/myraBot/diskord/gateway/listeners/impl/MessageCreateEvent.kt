package com.github.myraBot.diskord.gateway.listeners.impl

import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.gateway.listeners.Event

data class MessageCreateEvent(
        val message: Message
) : Event()