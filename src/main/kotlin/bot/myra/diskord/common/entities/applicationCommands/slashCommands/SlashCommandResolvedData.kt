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
    val users: HashMap<String, User> = hashMapOf(),
    val members: HashMap<String, MemberData> = hashMapOf(),
    val roles: HashMap<String, Role> = hashMapOf(),
    val channels: HashMap<String, ChannelData> = hashMapOf(),
    val messages: HashMap<String, Message> = hashMapOf(),
    val attachments: HashMap<String, Attachment> = hashMapOf()
)

data class Resolved(
    private val data: ResolvedData,
    val guildId: String
) {
    val users: HashMap<String, User> = data.users
    val members: HashMap<String, MemberData> = data.members
    val roles: HashMap<String, Role> = data.roles
    val channels: HashMap<String, ChannelData> = data.channels
    val messages: HashMap<String, Message> = data.messages
    val attachments: HashMap<String, Attachment> = data.attachments

    fun getUser(id: String): User? = data.users[id]
    fun getMember(id: String): Member? = data.members[id]?.let { Member.withUser(it, guildId, getUser(id)!!) }
    fun getRole(id: String): Role? = data.roles[id]
    fun getChannel(id: String): ChannelData? = data.channels[id]
    fun getMessage(id: String): Message? = data.messages[id]
    fun getAttachment(id: String): Attachment? = data.attachments[id]
}
