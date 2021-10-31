package com.github.myraBot.diskord.rest.builders

import com.github.myraBot.diskord.common.entityData.components.items.button.ButtonData
import com.github.myraBot.diskord.common.entityData.components.items.button.ButtonStyle

object Button {

    fun primary(id: String, data: ButtonData.() -> Unit = {}): ButtonData = ButtonData(style = ButtonStyle.PRIMARY, id = id).apply(data)
    fun secondary(id: String, data: ButtonData.() -> Unit = {}): ButtonData = ButtonData(style = ButtonStyle.SECONDARY, id = id).apply(data)
    fun success(id: String, data: ButtonData.() -> Unit = {}): ButtonData = ButtonData(style = ButtonStyle.SUCCESS, id = id).apply(data)
    fun danger(id: String, data: ButtonData.() -> Unit = {}): ButtonData = ButtonData(style = ButtonStyle.DANGER, id = id).apply(data)
    fun link(url: String, data: ButtonData.() -> Unit = {}): ButtonData = ButtonData(style = ButtonStyle.DANGER, url = url).apply(data)

}