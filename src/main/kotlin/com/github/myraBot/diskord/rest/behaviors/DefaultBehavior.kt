package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.rest.Endpoints

interface DefaultBehavior {

    suspend fun getApplication(): Application = Endpoints.getBotApplication.executeNonNull()
    val diskord: Diskord get() = Diskord

}