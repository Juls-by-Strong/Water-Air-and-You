package com.crotsertech.waterairandyoumvp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingNotification(
    val id: Int,
    val title: String,
    val body: String
)

@Serializable
data class PollResponse(
    val appointments: List<Appointment> = emptyList(),
    @SerialName("unpaid_count")
    val unpaidCount: Int = 0,
    val equipment: List<Equipment> = emptyList(),
    val notifications: List<PendingNotification> = emptyList()
)