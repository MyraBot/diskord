package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.entities.applicationCommands.components.SelectMenu
import bot.myra.diskord.common.entities.applicationCommands.components.SelectOption

/**
 * Builder for [SelectMenu]s.
 */
@Suppress("unused")
class SelectMenuBuilder {

    var id: String? = null
    val options: MutableList<SelectOption> = mutableListOf()
    var placeholder: String? = null
    var minValues: Int = 1
    var maxValues: Int = 1
    var disabled: Boolean = false

    fun addOption(option: SelectOption) = this.options.add(option)
    fun addOption(builder: SelectOption.() -> Unit) = this.options.add(SelectOption("", "").apply(builder)
        .apply { if (label.isEmpty() || value.isEmpty()) throw IllegalArgumentException("Label and value can't be empty") })

    fun addOptions(vararg options: SelectOption) = this.options.addAll(options)

    /**
     * Create a [SelectMenu] from a builder.
     *
     * @return Returns an equal [SelectMenu].
     */
    fun asSelectMenu(): SelectMenu = id?.let {
        val menu = SelectMenu()
        menu.id = it
        menu.options = options
        menu.placeholder = this.placeholder
        menu.minValues = this.minValues
        menu.maxValues = this.maxValues
        menu.disabled = this.disabled
        menu
    } ?: throw IllegalStateException("Missing id")

}