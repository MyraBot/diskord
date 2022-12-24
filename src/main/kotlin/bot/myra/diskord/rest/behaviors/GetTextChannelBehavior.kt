package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.channel.DmChannel
import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.common.entities.channel.VoiceChannel
import bot.myra.diskord.rest.EntityProvider
import bot.myra.diskord.rest.request.Result

interface GetTextChannelBehavior

suspend inline fun <reified T> GetTextChannelBehavior.getChannel(id: String): Result<T> = EntityProvider.getChannel(id).transformValue { data ->
    when (T::class) {
        ChannelData::class  -> data
        DmChannel::class    -> DmChannel(data)
        TextChannel::class  -> TextChannel(data)
        VoiceChannel::class -> VoiceChannel(data)
        else                -> throw IllegalStateException("Unknown channel to cast: ${T::class.simpleName}")
    } as T
}
