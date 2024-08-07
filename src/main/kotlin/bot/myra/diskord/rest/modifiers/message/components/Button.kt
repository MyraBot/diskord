package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.entities.applicationCommands.components.Button
import bot.myra.diskord.common.entities.applicationCommands.components.ButtonStyle

@Suppress("unused")
object Button {
    suspend fun primary(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.PRIMARY, id = id).also { data.invoke(it) }
    suspend fun secondary(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.SECONDARY, id = id).also { data.invoke(it) }
    suspend fun success(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.SUCCESS, id = id).also { data.invoke(it) }
    suspend fun danger(id: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.DANGER, id = id).also { data.invoke(it) }
    suspend fun link(url: String, data: suspend Button.() -> Unit = {}): Button = Button(style = ButtonStyle.LINK, url = url).also { data.invoke(it) }
}