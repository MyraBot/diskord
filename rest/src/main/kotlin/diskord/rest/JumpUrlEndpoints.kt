package diskord.rest

object JumpUrlEndpoints {
    private const val url = "https://discord.com/channels/{guild.id}/{channel.id}/{message.id}"

    fun get(guildId: String, channelId: String, messageId: String): String {
        return url
            .replace("{guild.id}", guildId)
            .replace("{channel.id}", channelId)
            .replace("{message.id}", messageId)
    }
}