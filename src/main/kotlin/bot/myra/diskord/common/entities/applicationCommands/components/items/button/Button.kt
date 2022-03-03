package bot.myra.diskord.common.entities.applicationCommands.components.items.button

import bot.myra.diskord.common.entities.Emoji
import bot.myra.diskord.common.entities.applicationCommands.components.items.ComponentType
import kotlinx.serialization.SerialName

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#button-object)
 *
 * @property type Component type.
 * @property style A [ButtonStyle] entry.
 * @property label Text that appears on the button
 * @property emoji An [Emoji] object, only requires [Emoji.name], [Emoji.id] and [Emoji.animated].
 * @property id Custom set id.
 * @property url An url for [ButtonStyle.LINK] buttons.
 * @property disabled Whether the button is disabled.
 */
data class Button(
        val type: ComponentType = ComponentType.BUTTON,
        var style: ButtonStyle,
        var label: String? = null,
        var emoji: Emoji? = null,
        @SerialName("custom_id") var id: String? = null,
        var url: String? = null,
        var disabled: Boolean = false
)