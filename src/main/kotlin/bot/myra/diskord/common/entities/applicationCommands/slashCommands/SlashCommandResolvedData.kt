package bot.myra.diskord.common.entities.applicationCommands.slashCommands

import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.message.Attachment
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.user.User
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-resolved-data-structure)
 *
 */
@Serializable
data class ResolvedData(
    val users: MutableMap<String, User> = mutableMapOf(),
    val members: MutableMap<String, MemberData> = mutableMapOf(),
    val roles: MutableMap<String, Role> = mutableMapOf(),
    val channels: MutableMap<String, ChannelData> = mutableMapOf(),
    val messages: MutableMap<String, Message> = mutableMapOf(),
    val attachments: MutableMap<String, Attachment> = mutableMapOf()
)

data class Resolved(
    private val data: ResolvedData,
    val guildId: String
) {
    val users: MutableMap<String, User> = data.users
    val members: MutableMap<String, MemberData> = data.members
    val roles: MutableMap<String, Role> = data.roles
    val channels: MutableMap<String, ChannelData> = data.channels
    val messages: MutableMap<String, Message> = data.messages
    val attachments: MutableMap<String, Attachment> = data.attachments

    fun getUser(id: String): User? = data.users[id]
    fun getMember(id: String): Member? = data.members[id]?.let { Member.withUser(it, guildId, getUser(id)!!) }
    fun getRole(id: String): Role? = data.roles[id]
    fun getChannel(id: String): ChannelData? = data.channels[id]
    fun getMessage(id: String): Message? = data.messages[id]
    fun getAttachment(id: String): Attachment? = data.attachments[id]
}
