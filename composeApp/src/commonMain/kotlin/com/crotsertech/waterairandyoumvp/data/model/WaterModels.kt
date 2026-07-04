package com.crotsertech.waterairandyoumvp.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class WaterTestResponse(
    val current: WaterTest? = null,
    val history: List<WaterTest> = emptyList()
)

@Serializable
data class WaterTest(
    val test_id: Int,
    val label: String,
    val test_date: String,
    @Serializable(with = BooleanIntSerializer::class)
    val is_current: Boolean = false,
    val pdf_url: String? = null
)

object BooleanIntSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BooleanInt", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        val input = decoder as? JsonDecoder ?: return decoder.decodeBoolean()
        val element = input.decodeJsonElement()
        return when (element) {
            is JsonPrimitive -> element.content.lowercase() in setOf("true", "1")
            else -> false
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeBoolean(value)
    }
}