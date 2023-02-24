package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Locale
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
    val channelId get() = data.channelId
    val user get() = User(data.memberData.value?.user ?: data.user.value!!, diskord)
    val token get() = data.token
    val version get() = data.version
    val message get() = data.message
    val locale get() = data.locale
    val guildLocale get() = data.guildLocale

    val modifier get() = data.modifier
    val followupModifier get() = data.followupModifier
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
    @SerialName("channel_id") val channelId: Optional<String> = Optional.Missing(),
    @SerialName("member") val memberData: Optional<PartialMemberData> = Optional.Missing(),
    val user: Optional<UserData> = Optional.Missing(),
    val token: String,
    val version: Int,
    val message: Optional<MessageData> = Optional.Missing(),
    val locale: Optional<Locale> = Optional.Missing(),
    @SerialName("guild_locale") val guildLocale: Optional<Locale> = Optional.Missing(),
) {
    val modifier get() = InteractionModifier(interaction = this)
    val followupModifier
        get() = InteractionModifier(interaction = this).apply {
            this@InteractionData.message.value?.let { message ->
                content = message.content
                tts = message.tts
                embeds = message.embeds
                components = message.components
                attachments = message.attachments.toMutableList()
            }
        }
}