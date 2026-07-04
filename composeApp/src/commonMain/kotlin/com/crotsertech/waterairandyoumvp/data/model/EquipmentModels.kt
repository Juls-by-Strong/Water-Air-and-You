package com.crotsertech.waterairandyoumvp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Equipment(
    val equipment_id: Int,
    val customer_id: Int? = null,
    val type_name: String,
    val model: String? = null,
    val serial_number: String? = null,
    val install_date: String? = null,
    val last_service_date: String? = null,
    val next_service_due: String? = null,
    val service_interval_days: Int? = null,
    val days_until_due: Int? = null,
    val status: String? = null,
    val location: String? = null,
    val notes: String? = null
) {
    val displayName: String = type_name
    val modelDisplay: String = model ?: ""
    val icon: String = when {
        type_name.lowercase().contains("softener") || type_name.lowercase().contains("conditioner") -> "🫧"
        type_name.lowercase().contains("reverse osmosis") || type_name.lowercase().contains(" ro ") || type_name.lowercase() == "ro" -> "💧"
        type_name.lowercase().contains("filter") -> "🔵"
        type_name.lowercase().contains("membrane") -> "🔘"
        type_name.lowercase().contains("air") || type_name.lowercase().contains("purifier") -> "💨"
        type_name.lowercase().contains("iron") || type_name.lowercase().contains("rust") -> "🟤"
        type_name.lowercase().contains("uv") || type_name.lowercase().contains("ultraviolet") -> "☀️"
        type_name.lowercase().contains("salt") -> "🧂"
        type_name.lowercase().contains("carbon") || type_name.lowercase().contains("chlorine") -> "⬛"
        type_name.lowercase().contains("pump") -> "⚙️"
        else -> "⚙️"
    }
    
    val dueStatus: DueStatus = when {
        days_until_due == null -> DueStatus.Unknown
        days_until_due!! < 0 -> DueStatus.Overdue
        days_until_due!! <= 30 -> DueStatus.Soon
        else -> DueStatus.Ok
    }
}

enum class DueStatus { Ok, Soon, Overdue, Unknown }

@Serializable
data class ServiceRecord(
    val record_id: Int,
    val equipment_id: Int,
    val service_date: String,
    val service_type: String,
    val notes: String? = null,
    val next_service_due: String? = null,
    val technician: String? = null
)

@Serializable
data class ServiceType(
    val type_id: Int,
    val name: String,
    val description: String? = null,
    val min_days_out: Int? = null
)

@Serializable
data class EquipmentSubComponent(
    val component_id: Int,
    val equipment_id: Int,
    val name: String,
    val model: String? = null,
    val install_date: String? = null,
    val last_service_date: String? = null,
    val next_service_due: String? = null,
    val service_interval_days: Int? = null
)