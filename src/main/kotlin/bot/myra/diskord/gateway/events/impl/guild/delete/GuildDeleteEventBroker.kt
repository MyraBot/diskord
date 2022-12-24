package bot.myra.diskord.gateway.events.impl.guild.delete

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.UnavailableGuild
import bot.myra.diskord.gateway.events.types.EventAction
import bot.myra.diskord.gateway.events.types.EventBroker

class GuildDeleteEventBroker(
    val guild: UnavailableGuild
) : EventBroker() {

    override suspend fun choose(): EventAction? {
        val shadowedGuild = Diskord.unavailableGuilds.any { it.key == guild.id }
        if (shadowedGuild) return null // Guild got removed by trust and safety team

        return when (guild.gotKicked) {
            true  -> GuildLeaveEvent(guild)
            false -> GuildUnloadEvent(guild)
        }
    }

}
