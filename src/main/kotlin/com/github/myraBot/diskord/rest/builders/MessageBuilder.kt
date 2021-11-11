package com.github.myraBot.diskord.rest.builders

import com.github.myraBot.diskord.common.entities.interaction.components.Component
import com.github.myraBot.diskord.common.entities.interaction.components.asComponent
import com.github.myraBot.diskord.common.entities.interaction.components.items.ActionRowData
import com.github.myraBot.diskord.common.entities.interaction.components.items.button.Button
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageBuilder(
        var content: String? = null,
        var tts: Boolean? = null,
        var embeds: MutableList<Embed> = mutableListOf(),
        @SerialName("components") val actionRows: MutableList<Component> = mutableListOf()
) {
    fun addEmbed(embed: Embed.() -> Unit) = embeds.add(Embed().apply(embed))
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
}