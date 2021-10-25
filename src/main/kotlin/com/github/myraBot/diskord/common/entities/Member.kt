package com.github.myraBot.diskord.common.entities

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.common.entityData.GuildData
import com.github.myraBot.diskord.common.entityData.MemberData

class Member(
        private val guildData: GuildData,
        private val memberData: MemberData,
        private val userData: UserData,
) {
    val id: String = userData.id
    val guild: Guild = Guild(guildData)
}