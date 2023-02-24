package bot.myra.diskord.common.entities.channel.text

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.rest.behaviors.Entity
import bot.myra.diskord.rest.behaviors.channel.TextChannelBehavior

open class GenericTextChannel(
    override val data: ChannelData,
    override val diskord: Diskord,
) : Entity, TextChannelBehavior {
    override val id get() = data.id
    val source get() = data.source
    val guildId get() = data.guildId
    val name get() = data.name
    val topic get() = data.topic
    val nsfw get() = data.nsfw
    val lastMessageId get() = data.lastMessageId
    val lastPinTimestamp get() = data.lastPinTimestamp
}