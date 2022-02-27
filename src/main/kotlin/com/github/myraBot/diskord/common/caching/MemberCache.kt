package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.events.ListenTo
import com.github.myraBot.diskord.gateway.events.impl.guild.MemberUpdateEvent
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
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

