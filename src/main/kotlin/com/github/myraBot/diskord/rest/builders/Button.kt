package com.github.myraBot.diskord.rest.builders

import com.github.myraBot.diskord.common.entities.interaction.components.items.button.Button
import com.github.myraBot.diskord.common.entities.interaction.components.items.button.ButtonStyle

object Button {

    fun primary(id: String, data: Button.() -> Unit = {}): Button = Button(style = ButtonStyle.PRIMARY, id = id)
        .apply(data)
    fun secondary(id: String, data: Button.() -> Unit = {}): Button = Button(style = ButtonStyle.SECONDARY, id = id)
        .apply(data)
    fun success(id: String, data: Button.() -> Unit = {}): Button = Button(style = ButtonStyle.SUCCESS, id = id)
        .apply(data)
    fun danger(id: String, data: Button.() -> Unit = {}): Button = Button(style = ButtonStyle.DANGER, id = id)
        .apply(data)
    fun link(url: String, data: Button.() -> Unit = {}): Button = Button(style = ButtonStyle.DANGER, url = url)
        .apply(data)

}