package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.GenericGuild

@Suppress("unused")
interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    suspend fun getGuild(): GenericGuild? = Diskord.getGuild(data.guildId.value!!)
}