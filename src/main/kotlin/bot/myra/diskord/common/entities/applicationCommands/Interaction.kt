package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.Permission
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.channel.GenericChannel
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.PartialMemberData
import bot.myra.diskord.common.entities.message.MessageData
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.common.entities.user.UserData
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.modifiers.InteractionModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Suppress("unused")
class Interaction(
    val diskord: Diskord,
    val data: InteractionData
) {
    val id get() = data.id
    val applicationId get() = data.applicationId
    val type get() = data.type
    val interactionData get() = data.data
    val guildId get() = data.guildId
    val channel get() = data.channel.value?.let { GenericChannel(it, diskord) }
    val user get() = User(data.memberData.value?.user ?: data.user.value!!, diskord)
    val token get() = data.token
    val version get() = data.version
    val message get() = data.message
    val locale get() = data.locale
    val guildLocale get() = data.guildLocale

    val modifier get() = InteractionModifier(this)
    val followupModifier
        get() = InteractionModifier(this).apply {
            val message = this@Interaction.message.value ?: return@apply
            content = message.content
            tts = message.tts
            embeds = message.embeds
            components = message.components
            attachments = message.attachments.toMutableList()
        }
    val member: Member?
        get() = data.memberData.value?.let {
            Member.fromPartialMember(it, guildId.value!!, diskord)
        }
}

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-structure)
 */
@Serializable
data class InteractionData(
    val id: String,
    @SerialName("application_id") val applicationId: String,
    val type: InteractionType,
    @SerialName("data") val data: Optional<JsonObject> = Optional.Missing(),
    @SerialName("guild_id") val guildId: Optional<String> = Optional.Missing(),
    val channel: Optional<ChannelData> = Optional.Missing(),
    @SerialName("member") val memberData: Optional<PartialMemberData> = Optional.Missing(),
    val user: Optional<UserData> = Optional.Missing(),
    val token: String,
    val version: Int,
    val message: Optional<MessageData> = Optional.Missing(),
    @Serializable(with = Permission.OptionalSerializer::class) @SerialName("app_permissions") val appPermissions: Optional<List<Permission>> = Optional.Missing(),
    val locale: Optional<Locale> = Optional.Missing(),
    @SerialName("guild_locale") val guildLocale: Optional<Locale> = Optional.Missing(),
)