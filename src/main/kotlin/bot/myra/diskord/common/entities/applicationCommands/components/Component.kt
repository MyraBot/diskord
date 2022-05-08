package bot.myra.diskord.common.entities.applicationCommands.components

import bot.myra.diskord.common.entities.Emoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/interactions/message-components#component-object-component-structure)
 */
@Serializable
open class Component(
    open val type: ComponentType,
    internal open var style: ButtonStyle? = null,
    internal open var label: String? = null,
    internal open var emoji: Emoji? = null,
    @SerialName("custom_id") internal open var id: String? = null,
    internal open var url: String? = null,
    internal open var disabled: Boolean = false,
    internal open var options:MutableList<SelectOption> = mutableListOf(),
    open var placeholder: String? = null,
    @SerialName("min_values") internal open val minValues: Int? = null,
    @SerialName("max_values") internal open val maxValues: Int? = null,
    internal open val components: MutableList<Component> = mutableListOf()
) {
    /**
     * @return Return a boolean whether the last [components] entry is full or not.
     */
    fun isFull(): Boolean {
        return when {
            this.components.size == 0                                                                          -> false
            this.components.find { it.type == ComponentType.BUTTON } != null && this.components.size == 5      -> true
            this.components.find { it.type == ComponentType.SELECT_MENU } != null && this.components.size == 1 -> true
            else                                                                                               -> false
        }
    }
}