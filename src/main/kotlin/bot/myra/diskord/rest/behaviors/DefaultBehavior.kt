package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.entities.Application
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.request.RestClient

interface DefaultBehavior {

    suspend fun getApplication(): Application = RestClient.execute(Endpoints.getBotApplication).getOrThrow()

}