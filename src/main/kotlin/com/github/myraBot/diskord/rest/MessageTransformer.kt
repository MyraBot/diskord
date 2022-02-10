package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import com.github.myraBot.diskord.rest.builders.InteractionMessageBuilder
import com.github.myraBot.diskord.rest.builders.MessageBuilder

interface MessageTransformer {
    suspend fun onEmbed(embed: Embed): Embed
    suspend fun onInteractionEmbed(interaction: Interaction, embed: Embed): Embed
    suspend fun onText(builder: MessageBuilder, text: String): String
}

object DefaultTransformer : MessageTransformer {
    override suspend fun onEmbed(embed: Embed): Embed = embed
    override suspend fun onInteractionEmbed(interaction: Interaction, embed: Embed): Embed = embed
    override suspend fun onText(builder: MessageBuilder, text: String): String = text
}

suspend fun MessageBuilder.transform(): MessageBuilder {
    val transform = Diskord.transformer

    embeds.onEach { embed -> transform.onEmbed(embed) }
    transformText()

    return this
}

suspend fun InteractionMessageBuilder.interactionTransform(): MessageBuilder {
    val transform = Diskord.transformer

    embeds.onEach { embed -> transform.onInteractionEmbed(this.interaction, embed) }
    transformText()

    return this
}

private suspend fun MessageBuilder.transformText() {
    val transform = Diskord.transformer

    content?.let { content = transform.onText(this, it) }
    embeds.onEach { embed ->
        transform.onEmbed(embed)

        embed.title?.let { embed.title == transform.onText(this, it) }
        embed.author?.name?.let { embed.author!!.name = transform.onText(this, it) }
        embed.description?.let { embed.description = transform.onText(this, it) }
        embed.fields.forEach { field ->
            field.name.let { field.name = transform.onText(this, it) }
            field.value.let { field.value = transform.onText(this, it) }
        }
        embed.footer?.text?.let { embed.footer!!.text = transform.onText(this, it) }
    }
}