package com.github.myraBot.diskord.common.entities

import com.github.myraBot.diskord.common.entityData.GuildData
import com.github.myraBot.diskord.rest.behaviors.GuildBehavior

class Guild(
        val data: GuildData
) : GuildBehavior {
    override val id: String = data.id
}