package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.entities.channel.MessageChannel
import com.github.myraBot.diskord.common.entityData.GuildData
import com.github.myraBot.diskord.common.entityData.message.MessageData
import com.github.myraBot.diskord.common.entityData.message.MessageFlag
import com.github.myraBot.diskord.common.entityData.components.Component
import com.github.myraBot.diskord.rest.behaviors.MessageBehavior

class Message(
        val data: MessageData,
) : MessageBehavior {
    val content: String = data.content
    val components: List<Component> = data.components
    val isWebhook: Boolean = data.webhookId == null
    val isSystem: Boolean = data.flags.contains(MessageFlag.URGENT)
    val user = User(data.user)
    val member: Member?
        get() = if (data.guildId == null) null
        else if (data.member == null) null
        else Member(data.guildId, data.member, data.user)
    val guild: Guild? get() = data.guildId?.let { Guild(GuildData(it)) }
    val channel: MessageChannel = MessageChannel(data.channelId)
}