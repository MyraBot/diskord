package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild

@Suppress("unused")
interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    suspend fun getGuild(): Guild? = Diskord.getGuild(data.guildId.value!!)
}