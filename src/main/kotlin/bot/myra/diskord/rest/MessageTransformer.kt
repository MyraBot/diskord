package bot.myra.diskord.rest

import bot.myra.diskord.common.entities.message.embed.Embed
import bot.myra.diskord.rest.modifiers.InteractionModifier
import bot.myra.diskord.rest.modifiers.message.components.MessageModifier

interface MessageTransformer {
    suspend fun onText(builder: MessageModifier, text: String): String
    suspend fun onEmbed(embed: Embed): Embed
    suspend fun onInteractionText(builder: InteractionModifier, text: String): String
    suspend fun onInteractionEmbed(interaction: InteractionModifier, embed: Embed): Embed
}

object DefaultTransformer : MessageTransformer {
    override suspend fun onText(builder: MessageModifier, text: String): String = text
    override suspend fun onEmbed(embed: Embed): Embed = embed
    override suspend fun onInteractionText(builder: InteractionModifier, text: String): String = text
    override suspend fun onInteractionEmbed(interaction: InteractionModifier, embed: Embed): Embed = embed
}