package com.github.myraBot.diskord.common.entities

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.common.entityData.MemberData

class Member(
        private val guildId: String,
        private val memberData: MemberData,
        private val userData: UserData,
) {
    /**
     * This constructor can be used if [MemberData.user] is not null.
     */
    constructor(
            guildId: String,
            memberData: MemberData
    ) : this(guildId, memberData, memberData.user!!)

    val id: String = userData.id
    val name: String get() = memberData.nick ?: userData.username
}