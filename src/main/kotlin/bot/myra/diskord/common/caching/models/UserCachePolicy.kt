package bot.myra.diskord.common.caching.models

import bot.myra.diskord.common.caching.GenericCachePolicy
import bot.myra.diskord.common.entities.user.User

class UserCachePolicy : GenericCachePolicy<String, User>()

