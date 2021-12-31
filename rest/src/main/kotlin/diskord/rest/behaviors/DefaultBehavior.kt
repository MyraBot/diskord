package diskord.rest.behaviors

import diskord.Diskord
import diskord.common.entities.Application
import diskord.rest.Endpoints

interface DefaultBehavior {

    suspend fun getApplication(): Application = Endpoints.getBotApplication.executeNonNull()
    val diskord: Diskord get() = Diskord

}