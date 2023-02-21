package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.rest.behaviors.channel.TextChannelBehavior

class DmChannel(
    override val data: ChannelData,
    override val diskord: Diskord
) : TextChannelBehavior {
    suspend fun getOwner() = diskord.getUser(data.ownerId.value!!)
}

