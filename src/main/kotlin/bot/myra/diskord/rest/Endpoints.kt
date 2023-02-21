package bot.myra.diskord.rest

import bot.myra.diskord.common.entities.Application
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandData
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.GuildData
import bot.myra.diskord.common.entities.guild.PartialMemberData
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.message.MessageData
import bot.myra.diskord.common.entities.user.UserData
import bot.myra.diskord.common.utilities.ListSerializer
import io.ktor.http.*
import kotlinx.serialization.builtins.serializer


object Endpoints {
    const val baseUrl = "https://discord.com/api/v8"

    val getGlobalApplicationCommands = Route(HttpMethod.Get, "/applications/{application.id}/commands", ListSerializer(SlashCommandData.serializer()))
    val createMessage = Route(HttpMethod.Post, "/channels/{channel.id}/messages", MessageData.serializer())
    val getChannel = Route(HttpMethod.Get, "/channels/{channel.id}", ChannelData.serializer())
    val getChannels = Route(HttpMethod.Get, "/guilds/{guild.id}/channels", ListSerializer(ChannelData.serializer()))
    val getUser = Route(HttpMethod.Get, "/users/{user.id}", UserData.serializer())
    val getGuildMember = Route(HttpMethod.Get, "/guilds/{guild.id}/members/{user.id}", PartialMemberData.serializer())
    val listGuildMembers = Route(HttpMethod.Get, "/guilds/{guild.id}/members?limit={limit}&after=0", ListSerializer(PartialMemberData.serializer()))
    val getBotApplication = Route(HttpMethod.Get, "/oauth2/applications/@me", Application.serializer())
    val acknowledgeInteraction = Route(HttpMethod.Post, "/interactions/{interaction.id}/{interaction.token}/callback", Unit.serializer())
    val acknowledgeOriginalResponse = Route(HttpMethod.Patch, "/webhooks/{application.id}/{interaction.token}/messages/@original", Unit.serializer())
    val getOriginalInteractionResponse = Route(HttpMethod.Get, "/webhooks/{application.id}/{interaction.token}/messages/@original", MessageData.serializer())
    val getGuild = Route(HttpMethod.Get, "/guilds/{guild.id}?with_counts=true", GuildData.serializer())
    val editMessage = Route(HttpMethod.Patch, "/channels/{channel.id}/messages/{message.id}", MessageData.serializer())
    val deleteMessage = Route(HttpMethod.Delete, "/channels/{channel.id}/messages/{message.id}", Unit.serializer())
    val addReaction = Route(HttpMethod.Put, "/channels/{channel.id}/messages/{message.id}/reactions/{emoji}/@me", Unit.serializer())
    val deleteAllReactions = Route(HttpMethod.Delete, "/channels/{channel.id}/messages/{message.id}/reactions", Unit.serializer())
    val addMemberRole = Route(HttpMethod.Put, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val removeMemberRole = Route(HttpMethod.Delete, "/guilds/{guild.id}/members/{user.id}/roles/{role.id}", Unit.serializer())
    val createDm = Route(HttpMethod.Post, "/users/@me/channels", ChannelData.serializer())
    val createGuildBan = Route(HttpMethod.Put, "/guilds/{guild.id}/bans/{user.id}", Unit.serializer())
    val removeGuildBan = Route(HttpMethod.Delete, "/guilds/{guild.id}/bans/{user.id}", Unit.serializer())
    val getChannelMessage = Route(HttpMethod.Get, "/channels/{channel.id}/messages/{message.id}", MessageData.serializer())
    val getChannelMessages = Route(HttpMethod.Get, "/channels/{channel.id}/messages", ListSerializer(MessageData.serializer()))
    val bulkDeleteMessages = Route(HttpMethod.Post, "/channels/{channel.id}/messages/bulk-delete", Unit.serializer())
    val modifyCurrentUser = Route(HttpMethod.Patch, "/users/@me", UserData.serializer())
    val modifyGuildRole = Route(HttpMethod.Patch, "/guilds/{guild.id}/roles/{role.id}", Role.serializer())
}