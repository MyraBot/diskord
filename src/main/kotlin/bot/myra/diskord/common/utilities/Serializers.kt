package bot.myra.diskord.common.utilities

import bot.myra.diskord.common.JSON
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import java.awt.Color

object ColorIntSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("color_int", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.blue + value.green * 256 + value.red * 65536)
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