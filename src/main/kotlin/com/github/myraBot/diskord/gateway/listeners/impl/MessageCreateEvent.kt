package com.github.myraBot.diskord.gateway.listeners.impl

import com.github.m5rian.discord.objects.entities.User
import com.github.myraBot.diskord.common.entities.Guild
import com.github.myraBot.diskord.common.entities.Member
import com.github.myraBot.diskord.common.entities.Message
import com.github.myraBot.diskord.common.entities.TextChannel
import com.github.myraBot.diskord.gateway.listeners.Event

data class MessageCreateEvent(
        val message: Message
) : Event() {
    val user: User get() = message.user
    val member: Member? get() = message.member
    val guild: Guild? get() = message.guild
    val channel: TextChannel = TextChannel(message.channelId)
}