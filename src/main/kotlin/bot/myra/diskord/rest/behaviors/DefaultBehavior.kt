package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Application
import bot.myra.diskord.rest.Endpoints

/**
 * Default behavior which provides all other behavior interfaces with the discord instance.
 */
interface DefaultBehavior {
    val diskord: Diskord

    suspend fun getApplication(): Application = diskord.rest.execute(Endpoints.getBotApplication).getOrThrow()
}