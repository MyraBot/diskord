package com.github.myraBot.diskord.gateway.listeners.impl.guild

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.gateway.listeners.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberRemoveEvent(
    val user: User,
    val guildId: String
) : Event()