package com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure)
 */
@Serializable
data class SlashCommandOptionData(
        val name: String,
        val type: SlashCommandOptionType,
        val value: JsonElement? = null,
        val options: List<SlashCommandOptionData> = emptyList(),
        val focused: Boolean? = null
)
