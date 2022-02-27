package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.entities.Application
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

interface DefaultBehavior {

    fun getApplicationAsync(): Deferred<Application> = RestClient.executeAsync(Endpoints.getBotApplication)

}