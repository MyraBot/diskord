package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.Diskord

interface Entity : DefaultBehavior, DiskordObject {
    val id: String
}
interface DiskordObject {
    val diskord: Diskord
}