package com.crotsertech.waterairandyoumvp.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

object LenientIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LenientInt", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val input = decoder as? JsonDecoder ?: return decoder.decodeInt()
        val element = input.decodeJsonElement()
        return when {
            element is JsonPrimitive && element.isString -> {
                element.content.trimEnd('0').trimEnd('.').toDoubleOrNull()?.toInt() ?: 0
            }
            element is JsonPrimitive && !element.isString -> {
                element.content.toDoubleOrNull()?.toInt() ?: 0
            }
            else -> 0
        }
    }

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }
}

object LenientDoubleSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LenientDouble", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double {
        val input = decoder as? JsonDecoder ?: return decoder.decodeDouble()
        val element = input.decodeJsonElement()
        return when {
            element is JsonPrimitive && element.isString -> {
                element.content.toDoubleOrNull() ?: 0.0
            }
            element is JsonPrimitive && !element.isString -> {
                element.content.toDoubleOrNull() ?: 0.0
            }
            else -> 0.0
        }
    }

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeDouble(value)
    }
}

@Serializable
data class Invoice(
    val invoice_id: Int,
    val invoice_number: String,
    val issue_date: String,
    val due_date: String? = null,
    val status: String,
    val subtotal: Double = 0.0,
    val tax_amount: Double = 0.0,
    val total: Double,
    val balance_due: Double? = null,
    val amount_paid: Double? = null,
    @Serializable(with = BooleanIntSerializer::class)
    val card_fee_enabled: Boolean? = null,
    val card_fee_amount: Double? = null,
    @SerialName("lines")
    val lineItems: List<InvoiceLine>? = null
) {
    val balance: Double = if (status == "paid") 0.0 else (balance_due ?: (total - (amount_paid ?: 0.0)))
    val isPayable: Boolean = status in setOf("sent", "partial", "overdue") && balance > 0.005
    val showBalance: Boolean = status != "paid" && balance < total - 0.005 && balance > 0.005
}

@Serializable
data class InvoiceLine(
    val line_id: Int,
    @SerialName("line_name")
    val description: String,
    @Serializable(with = LenientIntSerializer::class)
    val quantity: Int,
    @Serializable(with = LenientDoubleSerializer::class)
    val unit_price: Double,
    @SerialName("line_total")
    @Serializable(with = LenientDoubleSerializer::class)
    val total: Double
)

@Serializable
data class PaymentInitiateResponse(
    val pay_url: String,
    val total: Double,
    val card_fee_amount: Double
)

@Serializable
data class PaymentStatusResponse(
    val paid: Boolean,
    val balance_due: Double?
)