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
    val focused: AutoCompleteOption get() = findFocused(autoCompletion.options!!) ?: throw Exception("Couldn't find the focused value")
    val member: Member? = data.member


    private fun findFocused(options: List<AutoCompleteOption>): AutoCompleteOption? {
        for (option in options) {
            when (option.type) {
                SlashCommandOptionType.SUB_COMMAND_GROUP -> return findFocused(option.options!!)
                SlashCommandOptionType.SUB_COMMAND       -> return findFocused(option.options!!)
                else                                     -> if (option.focused == true) return option
            }
        }
        return null
    }

    private fun hasFocused(option: AutoCompleteOption): Boolean = findFocused(listOf(option)) != null

}