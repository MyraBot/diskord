package com.github.myraBot.diskord.common.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Documentation](https://discord.com/developers/docs/dispatch/field-values#predefined-field-values-accepted-locales)
 *
 * @property code IETF language tag of the locale.
 * @property languageName the fully written out name of the locale.
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable(with = Locale.Serializer::class)
enum class Locale(val code: String, val languageName: String) {
    ENGLISH_US("en-US", "English (United States)"),
    ENGLISH_GB("en-GB", "English (Great Britain)"),
    BULGARIAN("bg", "Bulgarian"),
    CHINESE_CHINA("zh-CN", "Chinese (China)"),
    CHINESE_TAIWAN("zh-TW", "Chinese (Taiwan)"),
    CROATIAN("hr", "Croatian"),
    CZECH("cs", "Czech"),
    DANISH("da", "Danish"),
    DUTCH("nl", "Dutch"),
    FINNISH("fi", "Finnish"),
    FRENCH("fr", "French"),
    GERMAN("de", "German"),
    GREEK("el", "Greek"),
    HINDI("hi", "Hindi"),
    HUNGARIAN("hu", "Hungarian"),
    ITALIAN("it", "Italian"),
    JAPANESE("ja", "Japanese"),
    KOREAN("ko", "Korean"),
    LITHUANIAN("lt", "Lithuanian"),
    NORWEGIAN("no", "Norwegian"),
    POLISH("pl", "Polish"),
    PORTUGUESE_BRAZIL("pt-BR", "Portuguese (Brazil)"),
    ROMANIAN("ro", "Romanian"),
    RUSSIAN("ru", "Russian"),
    SPANISH_SPAIN("es-ES", "Spanish (Spain)"),
    SPANISH_SWEDISH("sv-SE", "Swedish"),
    THAI("th", "Thai"),
    TURKISH("tr", "Turkish"),
    UKRAINIAN("uk", "Ukrainian"),
    VIETNAMESE("vi", "Vietnamese");

    internal object Serializer : KSerializer<Locale> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Locale", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: Locale) = encoder.encodeString(value.code)
        override fun deserialize(decoder: Decoder): Locale = decoder.decodeString().let { values().first { locale -> locale.code == it } }
    }

}