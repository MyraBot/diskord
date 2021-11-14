package com.github.myraBot.diskord.gateway.listeners.impl.guild.roles

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.gateway.listeners.Event
import kotlinx.serialization.SerialName

/**
 * [Documentation]()
 *
 */
class RoleDeleteEvent(
        @SerialName("guild_id") private val guildId: String,
        @SerialName("role_id") private val roleId: String
) : Event()