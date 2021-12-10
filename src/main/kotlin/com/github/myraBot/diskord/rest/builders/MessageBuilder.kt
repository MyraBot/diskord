package com.github.myraBot.diskord.rest.builders

import com.github.myraBot.diskord.DiskordBuilder
import com.github.myraBot.diskord.common.entities.applicationCommands.components.Component
import com.github.myraBot.diskord.common.entities.applicationCommands.components.asComponent
import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.ActionRowData
import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.button.Button
import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.button.SelectMenu
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MessageBuilder(
        var content: String? = null,
        var tts: Boolean? = null,
        var embeds: MutableList<Embed> = mutableListOf(),
        @SerialName("components") var actionRows: MutableList<Component> = mutableListOf()
) {
    @Transient
    var variables: suspend ArgumentBuilder.() -> Unit = {}

    suspend fun addEmbed(embed: suspend Embed.() -> Unit) = embeds.add(Embed().apply { embed.invoke(this) })
    fun addEmbed(embed: Embed) = embeds.add(embed)

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
        if (actionRows.size == 0) actionRows.add(ActionRowData().asComponent())
        else if (actionRows.last().isFull()) actionRows.add(ActionRowData().asComponent())

        this.actionRows.last().components.add(button.asComponent())
    }

    fun addButtons(vararg button: Button) = button.forEach { addButton(it) }


    suspend fun addSelectMenu(selectMenu: suspend SelectMenuBuilder.() -> Unit) {
        if (actionRows.size == 0) actionRows.add(ActionRowData().asComponent())
        else if (actionRows.last().isFull()) actionRows.add(ActionRowData().asComponent())

        this.actionRows.last().components.add(SelectMenuBuilder()
            .apply { selectMenu.invoke(this) }
            .asSelectMenu()
            .asComponent())
    }

    fun addSelectMenu(selectMenu: SelectMenu) {
        if (actionRows.size == 0) actionRows.add(ActionRowData().asComponent())
        else if (actionRows.last().isFull()) actionRows.add(ActionRowData().asComponent())

        this.actionRows.last().components.add(selectMenu.asComponent())
    }

    fun addSelectMenus(vararg selectMenus: SelectMenu) = selectMenus.forEach { addSelectMenu(it) }

    /**
     * Applies [DiskordBuilder.textTransform] function to all strings in the message.
     */
    suspend fun transform(): MessageBuilder {
        val func = DiskordBuilder.textTransform
        val args = ArgumentBuilder().apply { variables.invoke(this) }

        content?.let { content = func.invoke(it, args) }
        embeds.forEach { embed ->
            embed.description?.let { embed.description = func.invoke(it, args) }
            embed.fields.forEach {
                it.name = func.invoke(it.name, args)
                it.value = func.invoke(it.value, args)
            }
        }
        actionRows.forEach { actionRow ->
            actionRow.components.forEach { component ->
                component.label?.let { component.label = func.invoke(it, args) }
                component.placeholder?.let { component.placeholder = func.invoke(it, args) }
            }
        }

        return this
    }
}