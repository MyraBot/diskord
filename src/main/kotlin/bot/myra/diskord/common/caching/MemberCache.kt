package bot.myra.diskord.common.caching

import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.MemberUpdateEvent
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

object MemberCache : Cache<DoubleKey, Member>() {

    override fun retrieveAsync(key: DoubleKey): Deferred<Member?> {
        val future = CompletableDeferred<Member?>()
        RestClient.coroutineScope.launch {
            val memberData = RestClient.executeNullableAsync(Endpoints.getGuildMember) {
                arguments {
                    arg("guild.id", key.first)
                    arg("user.id", key.second)
                }
            }.await() ?: return@launch Unit.also { future.complete(null) }
            val member = Member.withUserInMember(memberData, key.second)
            future.complete(member)
        }
        return future
    }

    @ListenTo(MemberUpdateEvent::class)
    suspend fun onMemberUpdate(event: MemberUpdateEvent) {
        val guild = event.getGuildAsync().await()
        cache[DoubleKey(guild!!.id, event.member.id)] = event.member
    }

}

