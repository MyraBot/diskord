package com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/**
 * [Documentation](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-object-interaction-data-structure)
 */
@Serializable
data class SlashCommandDataOption(
        val name: String,
        val type: SlashCommandOptionType,
        val value: JsonPrimitive? = null,
        val options: List<SlashCommandDataOption> = emptyList(),
        val focused: Boolean? = null
)
