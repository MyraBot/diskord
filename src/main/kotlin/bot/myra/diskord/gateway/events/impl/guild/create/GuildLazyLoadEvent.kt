package bot.myra.diskord.gateway.events.impl.guild.create

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.PossibleUnavailableGuild

class GuildLazyLoadEvent(
    override val guild: PossibleUnavailableGuild,
    override val diskord: Diskord
) : GenericGuildCreateEvent(guild) {

    override suspend fun handle() {
        // TODO #asExtendedGuild doesn't work when the guild is a unavailable guild.
        diskord.unavailableGuilds.remove(guild.id)?.complete(guild.asExtendedGuild().asGuild())
    }

}