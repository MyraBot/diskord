package com.github.myraBot.diskord.rest

import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.message.embed.Embed
import com.github.myraBot.diskord.gateway.DiskordBuilder
import com.github.myraBot.diskord.rest.builders.MessageBuilder

interface MessageTransformer {
    suspend fun onEmbed(embed: Embed): Embed
    suspend fun onInteraction(interaction: Interaction, embed: Embed): Embed
}

object DefaultTransformer : MessageTransformer {
    override suspend fun onEmbed(embed: Embed): Embed = embed
    override suspend fun onInteraction(interaction: Interaction, embed: Embed): Embed = embed
}

suspend fun MessageBuilder.transform(): MessageBuilder {
    val t = DiskordBuilder.transformer
    embeds.onEach { embed ->
        t.onEmbed(embed)
    }
    return this
}