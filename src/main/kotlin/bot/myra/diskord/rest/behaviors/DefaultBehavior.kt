package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.entities.Application
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

interface DefaultBehavior {

    fun getApplicationAsync(): Deferred<Application> = RestClient.executeAsync(Endpoints.getBotApplication)

}