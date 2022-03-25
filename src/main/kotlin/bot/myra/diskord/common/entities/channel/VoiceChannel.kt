package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

data class VoiceChannel(
    override val data: ChannelData,
) : GuildChannel {

    fun getMembersAsync(): Deferred<List<Member>> {
        val future = CompletableDeferred<List<Member>>()
        RestClient.coroutineScope.launch {
            val voiceStates = Diskord.cachePolicy.voiceStateCachePolicy.view().filter { it.channelId === data.id }
            val members = voiceStates.map { state ->
                state.getMemberAsync()
            }.awaitAll()
            future.complete(members)
        }
        return future
    }

}