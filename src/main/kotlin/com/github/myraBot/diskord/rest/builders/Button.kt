package com.github.myraBot.diskord.rest.builders

import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.button.Button
import com.github.myraBot.diskord.common.entities.applicationCommands.components.items.button.ButtonStyle

@Suppress("unused")
object Button {
    suspend fun primary(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.PRIMARY, id = id).also { data.invoke(it) }
    suspend fun secondary(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.SECONDARY, id = id).also { data.invoke(it) }
    suspend fun success(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.SUCCESS, id = id).also { data.invoke(it) }
    suspend fun danger(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.DANGER, id = id).also { data.invoke(it) }
    suspend fun link(url: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.LINK, url = url).also { data.invoke(it) }
}