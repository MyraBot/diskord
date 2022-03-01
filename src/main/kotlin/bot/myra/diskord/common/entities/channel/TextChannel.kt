package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.rest.behaviors.channel.TextChannelBehavior

class TextChannel(
        override val data: ChannelData
) : GuildChannel, TextChannelBehavior