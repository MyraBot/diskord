package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.Arguments
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.components.Component
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import com.github.myraBot.diskord.common.entities.message.embed.Footer
import com.github.myraBot.diskord.gateway.DiskordBuilder
import com.github.myraBot.diskord.rest.builders.MessageBuilder

interface MessageTransformer {
    suspend fun beforeEmbed(embed: Embed): Embed
    suspend fun afterEmbed(embed: Embed): Embed

    suspend fun beforeInteractionEmbed(interaction: Interaction, embed: Embed): Embed
    suspend fun afterInteractionEmbed(interaction: Interaction, embed: Embed): Embed

    suspend fun onText(text: String, arguments: Arguments): String
}


object DefaultTransformer : MessageTransformer {
    override suspend fun beforeEmbed(embed: Embed): Embed = embed
    override suspend fun afterEmbed(embed: Embed): Embed = embed

    override suspend fun beforeInteractionEmbed(interaction: Interaction, embed: Embed): Embed = embed
    override suspend fun afterInteractionEmbed(interaction: Interaction, embed: Embed): Embed = embed

    override suspend fun onText(text: String, arguments: Arguments): String = text
}


suspend fun MessageBuilder.transform(): MessageBuilder {
    val t = DiskordBuilder.transformer
    val args = Arguments().apply { variables.invoke(this) }

    content?.let { content = t.onText(it, args) }
    embeds.onEach { embed ->
        t.beforeEmbed(embed)
        embed.transformText(args)
        t.afterEmbed(embed)
    }
    actionRows.transformText(args)

    return this
}

suspend fun MessageBuilder.interactionTransform(interaction: Interaction): MessageBuilder {
    val t = DiskordBuilder.transformer
    val args = Arguments().apply { variables.invoke(this) }

    content?.let { content = t.onText(it, args) }
    embeds.onEach { embed ->
        t.beforeInteractionEmbed(interaction, embed)
        embed.transformText(args)
        t.afterInteractionEmbed(interaction, embed)
    }
    actionRows.transformText(args)

    return this
}

private suspend fun Embed.transformText(args: Arguments) {
    val t = DiskordBuilder.transformer

    this.description?.let { this.description = t.onText(it, args) }
    this.fields.forEach {
        it.name = t.onText(it.name, args)
        it.value = t.onText(it.value, args)
    }
    this.footer?.let { this.footer = Footer(t.onText(it.text, args), it.icon) }
}

private suspend fun List<Component>.transformText(args: Arguments) {
    val t = DiskordBuilder.transformer

    onEach { actionRow ->
        actionRow.components.forEach { component ->
            component.label?.let { component.label = t.onText(it, args) }
            component.placeholder?.let { component.placeholder = t.onText(it, args) }
        }
    }
}