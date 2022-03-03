package bot.myra.diskord.rest.modifiers.message.components

import bot.myra.diskord.common.entities.message.embed.Embed

interface IEmbedModifier {
    val embeds: MutableList<Embed>

    suspend fun addEmbed(embed: suspend Embed.() -> Unit) = embeds.add(Embed().apply { embed.invoke(this) })

    fun addEmbed(embed: Embed) = embeds.add(embed)

}