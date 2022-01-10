package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.rest.behaviors.channel.TextChannelBehavior

class TextChannel(
        override val data: ChannelData
) : GuildChannel, TextChannelBehavior