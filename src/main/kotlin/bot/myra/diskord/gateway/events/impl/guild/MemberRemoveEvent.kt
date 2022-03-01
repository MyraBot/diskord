package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.entities.guild.RemovedMember
import bot.myra.diskord.gateway.events.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberRemoveEvent(
    val removedMember: RemovedMember
) : Event()