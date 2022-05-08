package bot.myra.diskord.common.entities.applicationCommands

import bot.myra.diskord.common.entities.Locale
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.guild.MemberData
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.rest.Optional
import bot.myra.diskord.rest.modifiers.InteractionModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-structure)
 */
@Serializable
data class Interaction(
    val id: String,
    @SerialName("application_id") val applicationId: String,
    val type: InteractionType,
    @SerialName("data") val data: Optional<JsonObject> = Optional.Missing(),
    @SerialName("guild_id") val guildId: Optional<String> = Optional.Missing(),
    @SerialName("channel_id") val channelId: Optional<String> = Optional.Missing(),
    @SerialName("member") val memberData: Optional<MemberData> = Optional.Missing(),
    val user: Optional<User> = Optional.Missing(),
    val token: String,
    val version: Int,
    val message: Optional<Message> = Optional.Missing(),
    val locale: Optional<Locale> = Optional.Missing(),
    @SerialName("guild_locale") val guildLocale: Optional<Locale> = Optional.Missing(),
) {
    val member: Member? get() = memberData.value?.let { Member.withUserInMember(it, guildId.value!!) }

    fun asModifier(): InteractionModifier = InteractionModifier(interaction = this)

    fun asFollowupModifier(): InteractionModifier = InteractionModifier(interaction = this).apply {
        this@Interaction.message.value?.let { message ->
            content = message.content
            tts = message.tts
            embeds = message.embeds
            components = message.components
            attachments = message.attachments.toMutableList()
        }
    }

}