package bot.myra.diskord.gateway.events.impl.guild.delete

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.GenericGuild
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.coroutines.runBlocking

@Suppress("unused")
abstract class GenericGuildDeleteEvent(
    open val guild: UnavailableGuild,
    var cachedGuild: GenericGuild? = runBlocking { Diskord.getGuild(guild.id) }
) : Event()
