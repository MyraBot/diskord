package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.GuildCreateEvent
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.Promise

object UserCache : Cache<String, User>(
    retrieve = { key ->
        Promise.of(Endpoints.getUser) { arg("user.id", key) }
    }
)

