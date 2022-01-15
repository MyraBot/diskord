package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.Promise

interface DefaultBehavior {

    fun getApplication(): Promise<Application> = Promise.of(Endpoints.getBotApplication)
    val diskord: Diskord get() = Diskord

}