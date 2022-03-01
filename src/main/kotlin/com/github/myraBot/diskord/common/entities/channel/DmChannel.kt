package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.rest.behaviors.channel.TextChannelBehavior
import kotlinx.coroutines.Deferred

class DmChannel(
        override val data: ChannelData
) : TextChannelBehavior {
     fun getOwner(): Deferred<User?> = Diskord.getUserAsync(data.ownerId.value!!)
}

