package com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands

import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.message.Message
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
        val channels: HashMap<String, Channel> = hashMapOf(),
        val messages: HashMap<String, Message> = hashMapOf()
)

data class Resolved(
        private val data: ResolvedData,
        val guildId: String
) {
    val users: HashMap<String, User> = data.users
    val members: HashMap<String, MemberData> = data.members
    val roles: HashMap<String, Role> = data.roles
    val channels: HashMap<String, Channel> = data.channels
    val messages: HashMap<String, Message> = data.messages

    fun getUser(id: String): User? = data.users[id]
    fun getMember(id: String): Member? = data.members[id]?.let { Member.withUser(it, guildId, getUser(id)!!) }
    fun getRole(id: String): Role? = data.roles[id]
    fun getChannel(id: String): Channel? = data.channels[id]
    fun getMessage(id: String): Message? = data.messages[id]
}
