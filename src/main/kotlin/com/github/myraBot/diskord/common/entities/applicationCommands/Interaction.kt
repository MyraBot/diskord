package com.github.myraBot.diskord.common.entities.applicationCommands

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.MemberData
import com.github.myraBot.diskord.common.entities.message.Message
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-structure)
 */
@Serializable
data class Interaction(
        val id: String,
        @SerialName("application_id") val applicationId: String,
        val type: InteractionType,
        @SerialName("data") val interactionDataJson: JsonObject? = null,
        @SerialName("guild_id") val guildId: String? = null,
        @SerialName("channel_id") val channelId: String? = null,
        val member: MemberData? = null,
        val user: User? = null,
        val token: String,
        val version: Int,
        val message: Message? = null
) {
    val interactionComponentData: InteractionComponentData? get() = interactionDataJson?.let { JSON.decodeFromJsonElement(it) }
}