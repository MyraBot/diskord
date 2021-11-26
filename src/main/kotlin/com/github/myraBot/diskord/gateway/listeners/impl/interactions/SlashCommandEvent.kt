package com.github.myraBot.diskord.gateway.listeners.impl.interactions

import com.github.myraBot.diskord.common.caching.ChannelCache
import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.applicationCommands.Interaction
import com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands.Resolved
import com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import com.github.myraBot.diskord.common.entities.applicationCommands.slashCommands.SlashCommandOptionType
import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.gateway.listeners.Event
import com.github.myraBot.diskord.rest.behaviors.InteractionCreateBehavior
import com.github.myraBot.diskord.utilities.JSON
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class SlashCommandEvent(
        override val interaction: Interaction
) : Event(), InteractionCreateBehavior {
    val command: SlashCommand get() = JSON.decodeFromJsonElement(interaction.interactionDataJson!!)
    val resolved: Resolved get() = Resolved(command.resolved, interaction.guildId!!)
    val member: Member get() = Member.withUserInMember(interaction.member!!, interaction.guildId!!)
    val guild: Guild get() = GuildCache[interaction.guildId!!]!!
    val channel: TextChannel get() = ChannelCache.getAs<TextChannel>(interaction.channelId!!)!!

    inline fun <reified T> getOption(name: String): T? {
        val option = command.options.firstOrNull { it.name == name && it.type == SlashCommandOptionType.fromClass<T>() }
        return option?.value?.let {
            when (T::class) {
                String::class -> option.value.jsonPrimitive.content as T
                Int::class -> option.value.jsonPrimitive.int as T
                Boolean::class -> option.value.jsonPrimitive.boolean as T
                User::class -> resolved.getUser(option.value.jsonPrimitive.content) as T
                Member::class -> resolved.getMember(option.value.jsonPrimitive.content) as T
                Channel::class -> resolved.getChannel(option.value.jsonPrimitive.content) as T
                Role::class -> resolved.getRole(option.value.jsonPrimitive.content) as T
                Unit::class -> TODO() //TODO type -> Mentionable
                Long::class -> option.value.jsonPrimitive.long as T
                else -> throw Exception("Couldn't parse ${option.type} to a class")
            }
        }
    }

}