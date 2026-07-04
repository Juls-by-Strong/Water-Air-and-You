package com.crotsertech.waterairandyoumvp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val customer_id: Int,
    val role: String,
    val expires_in: Int? = null
)

@Serializable
data class RefreshRequest(
    val refresh_token: String
)

@Serializable
data class RefreshResponse(
    val access_token: String,
    val refresh_token: String
)

@Serializable
data class Customer(
    val customer_id: Int,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val role: String? = null
)

@Serializable
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String
)

@Serializable
data class ChangePasswordResponse(
    val message: String
)

@Serializable
data class PushSubscription(
    val endpoint: String,
    val p256dh: String? = null,
    val auth: String? = null
)

@Serializable
data class SaltDeliveryRequest(
    val bags: Int,
    val requested_date: String,
    val requested_window: String,
    val notes: String? = null
)
