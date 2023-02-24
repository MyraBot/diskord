package bot.myra.diskord.common.entities.channel.text

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.ChannelData

class DmChannel(
    override val data: ChannelData,
    override val diskord: Diskord
) : GenericTextChannel(data, diskord) {
    suspend fun getOwner() = diskord.getUser(data.ownerId.value!!)
}

