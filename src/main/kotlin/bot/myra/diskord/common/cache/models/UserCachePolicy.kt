package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.entities.user.UserData

class MutableUserCachePolicy : UserCachePolicy()
class DisabledUserCachePolicy : UserCachePolicy()

abstract class UserCachePolicy : GenericCachePolicy<String, UserData>() {
    override fun getAsKey(value: UserData): String = value.id
}

