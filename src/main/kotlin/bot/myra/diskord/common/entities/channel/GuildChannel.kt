package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord

@Suppress("unused")
interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    suspend fun getGuild() = Diskord.getGuild(data.guildId.value!!)
}