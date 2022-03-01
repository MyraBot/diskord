package bot.myra.diskord.common.caching

import bot.myra.diskord.common.entities.User
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

object UserCache : Cache<String, User>() {

    override fun retrieveAsync(key: String): Deferred<User?> {
        return RestClient.executeNullableAsync(Endpoints.getUser) {
            arguments { arg("user.id", key) }
        }
    }

}

