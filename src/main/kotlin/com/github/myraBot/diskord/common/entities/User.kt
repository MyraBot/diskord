package com.github.myraBot.diskord.common.entities

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.common.CdnEndpoints

class User(
        val data: UserData
) {
    val id: String = data.id
    val name: String = data.username
    val discriminator: String = data.discriminator
    val isBot: Boolean = data.bot
    val avatar: String get() = CdnEndpoints.userAvatar.apply { arg("user_id", data.id); arg("user_avatar", data.avatarHash) }
}