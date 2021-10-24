package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.entityData.GuildData
import com.github.myraBot.diskord.common.entityData.message.MessageData
import com.github.myraBot.diskord.rest.behaviors.MessageBehavior
import com.github.myraBot.diskord.rest.behaviors.getChannel

class Message(
        val data: MessageData,
) : MessageBehavior {
    val content: String = data.content
    val user = User(data.user)
    val member: Member?
        get() = if (data.guildId == null) null
        else if (data.member == null) null
        else Member(GuildData(data.guildId), data.member)
    val guild: Guild? get() = data.guildId?.let { Guild(GuildData(it)) }
    suspend fun channel(): TextChannel = getChannel(data.channelId)
}