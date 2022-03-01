package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.rest.behaviors.channel.TextChannelBehavior
import kotlinx.coroutines.Deferred

class DmChannel(
        override val data: ChannelData
) : TextChannelBehavior {

     fun getOwnerAsync(): Deferred<User?> = Diskord.getUserAsync(data.ownerId.value!!)

}

