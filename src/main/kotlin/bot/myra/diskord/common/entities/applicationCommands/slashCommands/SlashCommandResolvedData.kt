package bot.myra.diskord.common.entities.applicationCommands.slashCommands

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.PartialMemberData
import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.common.entities.message.Attachment
import bot.myra.diskord.common.entities.message.MessageData
import bot.myra.diskord.common.entities.user.UserData
import kotlinx.serialization.Serializable

// TODO Hmm improve it pls

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-resolved-data-structure)
 *
 */
@Serializable
data class ResolvedData(
    val users: MutableMap<String, UserData> = mutableMapOf(),
    val members: MutableMap<String, PartialMemberData> = mutableMapOf(),
    val roles: MutableMap<String, Role> = mutableMapOf(),
    val channels: MutableMap<String, ChannelData> = mutableMapOf(),
    val messages: MutableMap<String, MessageData> = mutableMapOf(),
    val attachments: MutableMap<String, Attachment> = mutableMapOf()
)

data class Resolved(
    private val data: ResolvedData,
    val guildId: String,
    val diskord: Diskord
) {
    val users: MutableMap<String, UserData> = data.users
    val members: MutableMap<String, PartialMemberData> = data.members
    val roles: MutableMap<String, Role> = data.roles
    val channels: MutableMap<String, ChannelData> = data.channels
    val messages: MutableMap<String, MessageData> = data.messages
    val attachments: MutableMap<String, Attachment> = data.attachments

    fun getUser(id: String): UserData? = data.users[id]
    fun getMember(id: String): Member? = data.members[id]?.let { Member.fromPartialMember(it, guildId, getUser(id)!!, diskord) }
    fun getRole(id: String): Role? = data.roles[id]
    fun getChannel(id: String): ChannelData? = data.channels[id]
    fun getMessage(id: String): MessageData? = data.messages[id]
    fun getAttachment(id: String): Attachment? = data.attachments[id]
}
