package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.common.utilities.Mention

interface Channel : Entity {
    val data: ChannelData

    override val id: String get() = data.id
    val source: ChannelType get() = data.source

    val mention: String get() = Mention.channel(id)
}