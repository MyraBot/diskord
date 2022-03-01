package com.github.myraBot.diskord.gateway.events.impl.guild

import com.github.myraBot.diskord.common.entities.guild.RemovedMember
import com.github.myraBot.diskord.gateway.events.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberRemoveEvent(
    val removedMember: RemovedMember
) : Event()