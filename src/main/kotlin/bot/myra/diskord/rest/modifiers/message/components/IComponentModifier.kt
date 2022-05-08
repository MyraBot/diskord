package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.entities.applicationCommands.components.*
import bot.myra.diskord.common.entities.applicationCommands.components.Button

interface IComponentModifier {
    val components: MutableList<Component>

    /**
     * Adds a button as a message component. If an action row already exists,
     * the button is appended to the existing action row. When the action row
     * is full, a new action row will be created and the button will be appended
     * to the new one. If there is no action row at all, it will also create a
     * new one and append the new button there.
     *
     * @param button The button to add as a component.
     */
    fun addButton(button: Button) {
        if (components.size == 0) components.add(ActionRow())
        else if (components.last().isFull()) components.add(ActionRow())

        this.components.last().components.add(button)
    }

    suspend fun addButton(style: ButtonStyle, builder: suspend Button.() -> Unit) = addButton(Button(style = style).apply { builder.invoke(this) })
    fun addButtons(vararg button: Button) = button.forEach { addButton(it) }
    fun addButtons(buttons: List<Button>) = buttons.forEach { addButton(it) }

    suspend fun addSelectMenu(selectMenu: suspend SelectMenuBuilder.() -> Unit) {
        if (components.size == 0) components.add(ActionRow())
        else if (components.last().isFull()) components.add(ActionRow())

        this.components.last().components.add(SelectMenuBuilder()
            .apply { selectMenu.invoke(this) }
            .asSelectMenu())
    }

    fun addSelectMenu(selectMenu: SelectMenu) {
        if (components.size == 0) components.add(ActionRow())
        else if (components.last().isFull()) components.add(ActionRow())

        this.components.last().components.add(selectMenu)
    }

    fun addSelectMenus(vararg selectMenus: SelectMenu) = selectMenus.forEach { addSelectMenu(it) }
}