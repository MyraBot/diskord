package bot.myra.diskord.gateway.events.impl.interactions.slashCommands

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.Role
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.applicationCommands.Interaction
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.Resolved
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandOptionData
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommandOptionType
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.events.impl.interactions.GenericInteractionCreateEvent
import bot.myra.diskord.rest.EntityProvider
import bot.myra.diskord.rest.behaviors.getChannelAsync
import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.*

open class SlashCommandEvent(
    override val data: Interaction,
) : GenericInteractionCreateEvent(data) {
    val command: SlashCommand get() = JSON.decodeFromJsonElement(data.interactionDataJson.value!!)
    val resolved: Resolved get() = Resolved(command.resolved, data.guildId.value!!)
    open val member: Member? get() = data.member
    val arguments: List<SlashCommandOptionData>
        get() = command.options
            .flatMap {
                var options = it.options
                while (
                    options.firstOrNull()?.type == SlashCommandOptionType.SUB_COMMAND_GROUP
                    || options.firstOrNull()?.type == SlashCommandOptionType.SUB_COMMAND
                ) {
                    options = options.first().options
                }
                options
            }

    open suspend fun getGuildAsync(): Deferred<Guild?> = EntityProvider.getGuild(data.guildId.value!!)
    fun getChannelAsync(): Deferred<TextChannel?> = Diskord.getChannelAsync(data.channelId.value!!)

    inline fun <reified T> getOption(name: String): T? {
        val option: SlashCommandOptionData? = arguments.firstOrNull { it.name == name }
        return option?.value?.let {
            when (T::class.java) {
                String::class.java -> option.value.jsonPrimitive.content
                Int::class.java -> option.value.jsonPrimitive.int
                Boolean::class.java -> option.value.jsonPrimitive.boolean
                User::class.java -> resolved.getUser(option.value.jsonPrimitive.content)
                Member::class.java -> resolved.getMember(option.value.jsonPrimitive.content)
                ChannelData::class.java -> resolved.getChannel(option.value.jsonPrimitive.content)
                Role::class.java -> resolved.getRole(option.value.jsonPrimitive.content)
                Unit::class.java -> TODO() // TODO type -> Mentionable
                Long::class.java -> option.value.jsonPrimitive.long
                else -> throw Exception("Couldn't parse ${option.type} to a class")
            } as T
        }
    }

}