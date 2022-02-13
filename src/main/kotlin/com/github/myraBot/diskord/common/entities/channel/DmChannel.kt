package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.rest.behaviors.channel.TextChannelBehavior
import com.github.myraBot.diskord.rest.request.promises.Promise
import kotlinx.serialization.Serializable

class DmChannel(
        override val data: ChannelData
) : TextChannelBehavior {
     suspend fun getOwner(): Promise<User> = Diskord.getUser(data.ownerId.value!!)
}

