package bot.myra.diskord.common.utilities

object MessageLink {

    fun guild(guildId: String, channelId: String, messageId: String): String {
        return "https://discord.com/channels/{guild.id}/{channel.id}/{message.id}"
            .replace("{guild.id}", guildId)
            .replace("{channel.id}", channelId)
            .replace("{message.id}", messageId)
    }

    fun dms(channelId: String, messageId: String): String {
        return "https://discord.com/channels/@me/{channel.id}/{message.id}"
            .replace("{channel.id}", channelId)
            .replace("{message.id}", messageId)
    }

}