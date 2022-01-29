package com.github.myraBot.diskord.rest.behaviors.guild

import com.github.myraBot.diskord.common.caching.DoubleKey
import com.github.myraBot.diskord.common.caching.MemberCache
import com.github.myraBot.diskord.common.caching.RoleCache
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.behaviors.Entity
import com.github.myraBot.diskord.rest.behaviors.GetTextChannelBehavior
import com.github.myraBot.diskord.rest.request.promises.Promise

interface GuildBehavior : Entity, GetTextChannelBehavior {

    fun getMember(id: String): Promise<Member> = MemberCache[DoubleKey(this.id, id)]
    fun getBotMember(): Promise<Member> = getApplication().then { getMember(it!!.id) }
    fun getMembers(limit: Int = 1000): Promise<List<Member>> = Promise.of(Endpoints.listGuildMembers) {
        arg("guild.id", this@GuildBehavior.id)
        arg("limit", limit)
    }.map { members ->
        members?.map { Member.withUserInMember(it, this.id) }
    }

    fun getRoles(): Promise<List<Role>> = Promise.of(Endpoints.getRoles) { arg("guild.id", this@GuildBehavior.id) }
    fun getRole(id: String): Promise<Role> = RoleCache[DoubleKey(this.id, id)]
    fun getChannels(): Promise<List<ChannelData>> = Promise.of(Endpoints.getChannels) { arg("guild.id", this@GuildBehavior.id) }
}