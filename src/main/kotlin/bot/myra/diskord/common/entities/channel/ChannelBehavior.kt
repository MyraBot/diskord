package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.utilities.Mention
import bot.myra.diskord.rest.behaviors.Entity

interface ChannelBehavior : Entity {
    val data: ChannelData
    val mention: String get() = Mention.channel(id)
}