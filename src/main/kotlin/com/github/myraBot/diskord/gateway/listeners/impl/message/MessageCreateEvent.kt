package com.github.myraBot.diskord.gateway.listeners.impl.message

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.gateway.listeners.Event

abstract class MessageCreateEvent(val message: Message) : Event() {
    val content: String = message.content
    val isWebhook: Boolean = message.isWebhook
    val isSystem: Boolean = message.isSystem
    val user: User = message.user
}