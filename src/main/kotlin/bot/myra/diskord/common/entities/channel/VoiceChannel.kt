package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

data class VoiceChannel(
    override val data: ChannelData,
) : GuildChannel {

    suspend fun getMembers(): List<Member> {
        val voiceStates = Diskord.cachePolicy.voiceStateCache.view().filter { it.channelId === data.id }
        return voiceStates.map {
            val future = CompletableDeferred<Member?>()
            RestClient.coroutineScope.launch { future.complete(it.getMember()) }
            future
        }.awaitAll().filterNotNull()
    }

}