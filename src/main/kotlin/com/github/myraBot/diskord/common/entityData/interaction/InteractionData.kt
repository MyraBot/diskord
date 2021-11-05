package com.github.myraBot.diskord.common.entityData.interaction

import com.github.m5rian.discord.objects.entities.UserData
import com.github.myraBot.diskord.common.entityData.MemberData
import com.github.myraBot.diskord.common.entityData.interaction.interactionTypes.InteractionComponentData
import com.github.myraBot.diskord.common.entityData.message.MessageData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-structure)
 */
@Serializable
data class InteractionData(
        val id: String,
        @SerialName("application_id") val applicationId: String,
        val type: InteractionType,
        @SerialName("data") val interactionData: InteractionComponentData? = null,
        @SerialName("guild_id") val guildId: String? = null,
        @SerialName("channel_id") val channelId: String? = null,
        val member: MemberData? = null,
        val user: UserData? = null,
        val token: String,
        val version: Int,
        val message: MessageData? = null
)