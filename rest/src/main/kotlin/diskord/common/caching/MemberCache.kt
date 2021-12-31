package diskord.common.caching

import diskord.common.entities.guild.Member
import diskord.gateway.listeners.ListenTo
import diskord.gateway.listeners.impl.guild.MemberUpdateEvent
import diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking

object MemberCache : DoubleKeyCache<String, String, Member>() {

    override fun retrieve(firstKey: String, secondKey: String): Member? {
        val member = runBlocking {
            Endpoints.getGuildMember.execute {
                arg("guild.id", firstKey)
                arg("user.id", secondKey)
            }
        }
        return member?.let { Member.withUserInMember(member, firstKey) }
    }

    @ListenTo(MemberUpdateEvent::class)
    fun onMemberUpdate(event: MemberUpdateEvent) = update(event.member.guild.id, event.member.id, event.member)

    override fun update(firstKey: String, secondKey: String, value: Member) {
        map[Key(firstKey, secondKey)] = value
    }

}