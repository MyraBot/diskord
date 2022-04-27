package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.entities.applicationCommands.components.items.button.SelectMenu
import bot.myra.diskord.common.entities.applicationCommands.components.items.selectMenu.SelectOption

class SelectMenuBuilder {

    lateinit var id: String
    val options: MutableList<SelectOption> = mutableListOf()
    var placeholder: String? = null
    var minValues: Int = 1
    var maxValues: Int = 1
    var disabled: Boolean = false

    fun addOption(option: SelectOption) = this.options.add(option)
    fun addOption(builder: SelectOption.() -> Unit) = this.options.add(SelectOption("", "").apply(builder)
        .apply { if (label.isEmpty() || value.isEmpty()) throw IllegalArgumentException("Label and value can't be empty") })

    fun addOptions(vararg options: SelectOption) = this.options.addAll(options)

    fun asSelectMenu(): SelectMenu = SelectMenu(
        id = this.id,
        options = this.options,
        placeholder = this.placeholder,
        minValues = this.minValues,
        maxValues = this.maxValues,
        disabled = this.disabled
    )

}