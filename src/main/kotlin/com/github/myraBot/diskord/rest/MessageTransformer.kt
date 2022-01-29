package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import com.github.myraBot.diskord.gateway.DiskordBuilder
import com.github.myraBot.diskord.rest.builders.MessageBuilder

interface MessageTransformer {
    suspend fun onEmbed(embed: Embed): Embed
    suspend fun onInteraction(interaction: Interaction, embed: Embed): Embed
    suspend fun onText(text: String): String
}

object DefaultTransformer : MessageTransformer {
    override suspend fun onEmbed(embed: Embed): Embed = embed
    override suspend fun onInteraction(interaction: Interaction, embed: Embed): Embed = embed
    override suspend fun onText(text: String): String = text
}

suspend fun MessageBuilder.transform(): MessageBuilder {
    val t = DiskordBuilder.transformer

    content?.apply { t.onText(this) }
    embeds.onEach { embed ->
        t.onEmbed(embed)

        embed.title?.apply { t.onText(this) }
        embed.author?.name?.apply { t.onText(this) }
        embed.description?.apply { t.onText(this) }
        embed.fields.forEach { field ->
            field.name.apply { t.onText(this) }
            field.value.apply { t.onText(this) }
        }
        embed.footer?.text?.apply { t.onText(this) }
    }

    return this
}