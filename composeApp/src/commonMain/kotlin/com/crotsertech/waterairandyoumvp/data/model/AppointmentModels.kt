package com.crotsertech.waterairandyoumvp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val appointment_id: Int,
    val customer_id: Int = 0,
    val service_type_id: Int = 0,
    val service_type: String,
    val status: String,
    val requested_date: String? = null,
    val requested_window: String? = null,
    val confirmed_date: String? = null,
    val confirmed_time: String? = null,
    val scheduled_date: String? = null,
    val customer_notes: String? = null,
    val equipment: List<AppointmentEquipment> = emptyList()
) {
    val displayDate: String = confirmed_date ?: requested_date ?: ""
    val displayTime: String = confirmed_time ?: ""
    val displayWindow: String = requested_window ?: ""
    
    val statusDisplay: String = when (status) {
        "in_progress" -> "In Progress"
        else -> status.replace('_', ' ').split(' ').joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
    }
    
    val isActive: Boolean = status in setOf("pending", "confirmed", "in_progress")
    val isUpcoming: Boolean = isActive
}

@Serializable
data class AppointmentEquipment(
    val equipment_id: Int,
    val type_name: String
)

@Serializable
data class CreateAppointmentRequest(
    val service_type_id: Int,
    val requested_date: String,
    val requested_window: String,
    val customer_notes: String?,
    val equipment_ids: List<Int>,
    val salt_delivery: Boolean,
    val oxyblast: Boolean
)