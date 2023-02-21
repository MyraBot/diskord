package bot.myra.diskord.common.entities.channel

@Suppress("unused")
interface GuildChannelBehavior : ChannelBehavior {
    override val data: ChannelData

    suspend fun getGuild() = diskord.getGuild(data.guildId.value!!)
}