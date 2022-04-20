package bot.myra.diskord.gateway.events.impl.interactions

import bot.myra.diskord.common.entities.applicationCommands.AutoCompleteOption
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandOptionType
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.rest.behaviors.AutoCompleteBehavior
import kotlinx.serialization.json.decodeFromJsonElement

data class AutoCompleteEvent(
    override val data: Interaction,
) : GenericInteractionCreateEvent(data), AutoCompleteBehavior {
    val autoCompletion: AutoCompleteOption get() = JSON.decodeFromJsonElement(data.interactionDataJson.value!!)
    val command: AutoCompleteOption get() = autoCompletion
    val subcommandGroup: AutoCompleteOption? get() = autoCompletion.options!!.firstOrNull { it.type == SlashCommandOptionType.SUB_COMMAND_GROUP && hasFocused(it) }
    val subcommand: AutoCompleteOption? get() = (subcommandGroup?.options ?: autoCompletion.options!!).firstOrNull { it.type == SlashCommandOptionType.SUB_COMMAND && hasFocused(it) }
    val option: AutoCompleteOption?
        get() = (subcommand?.options ?: autoCompletion.options!!).firstOrNull { it.type != SlashCommandOptionType.SUB_COMMAND_GROUP && it.type != SlashCommandOptionType.SUB_COMMAND && hasFocused(it) }
    val focused: AutoCompleteOption get() = findFocused(autoCompletion.options!!)
    val member: Member? = data.member

    private val subcommandTypes = listOf(SlashCommandOptionType.SUB_COMMAND_GROUP, SlashCommandOptionType.SUB_COMMAND)
    private fun findFocused(options: List<AutoCompleteOption>): AutoCompleteOption =
        options.firstOrNull { it.type in subcommandTypes }?.let { findFocused(it.options!!) }
            ?: options.first { it.focused == true }

    private fun hasFocused(option: AutoCompleteOption): Boolean {
        return if (option.type in subcommandTypes) option.options!!.any { hasFocused(it) }
        else option.focused == true
    }

}