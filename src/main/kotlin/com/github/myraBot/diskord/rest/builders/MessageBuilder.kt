package com.github.myraBot.diskord.rest.builders

import com.github.m5rian.discord.objects.entities.Embed
import kotlinx.serialization.Serializable

@Serializable
data class MessageBuilder(
        var content: String? = null,
        var tts: Boolean? = null,
        var embeds: MutableList<Embed> = mutableListOf()
) {
    fun addEmbed(embed: Embed.() -> Unit) {
        Embed().apply(embed)
    }
}