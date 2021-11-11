package com.github.myraBot.diskord.utilities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import java.awt.Color
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = Date.from(value)
        encoder.encodeString(formatter.format(date))
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

object ColourSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.rgb)
    override fun deserialize(decoder: Decoder): Color = Color.decode(decoder.decodeInt().toString())
}

class ListSerializer<T>(val serializer: KSerializer<T>) : KSerializer<List<T>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serializer.descriptor.serialName, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<T> {
        val roles = decoder.decodeSerializableValue(JsonArray.serializer())
        return roles.map { JSON.decodeFromJsonElement(serializer, it.jsonObject) }
    }

    override fun serialize(encoder: Encoder, value: List<T>) {
        val jsonArray = JsonArray(value.map { JSON.encodeToJsonElement(serializer, it) })
        encoder.encodeString(jsonArray.toString())
    }
}