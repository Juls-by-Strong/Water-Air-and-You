package com.crotsertech.waterairandyoumvp.data.api

import com.crotsertech.waterairandyoumvp.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ApiService {
    const val BASE_URL = "https://waterairandyoumvp.myusa.cloud/api/public/"
    private val jsonInstance = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _currentCustomer = MutableStateFlow<Customer?>(null)
    val currentCustomer = _currentCustomer.asStateFlow()

    // Separate client for auth to avoid infinite loops/deadlocks in Auth plugin
    private val authClient = createPlatformHttpClient {
        install(ContentNegotiation) {
            json(jsonInstance)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
        defaultRequest {
            url(BASE_URL)
            header("Accept", "application/json")
        }
    }

    private val client: HttpClient = createPlatformHttpClient {
        install(ContentNegotiation) {
            json(jsonInstance)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = TokenRepository.accessToken
                    val refresh = TokenRepository.refreshToken
                    if (token.isNotBlank()) {
                        BearerTokens(token, refresh)
                    } else {
                        null
                    }
                }
                sendWithoutRequest { request ->
                    "auth" in request.url.encodedPath
                }
            }
        }
        defaultRequest {
            url(BASE_URL)
            header("Accept", "application/json")
        }
    }

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val hasToken = TokenRepository.accessToken.isNotBlank()
        _isLoggedIn.value = hasToken
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authClient.post("auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            handleResponse<AuthResponse>(response).onSuccess {
                TokenRepository.saveTokens(it.access_token, it.refresh_token, it.customer_id)
                _isLoggedIn.value = true
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        TokenRepository.clearTokens()
        _currentCustomer.value = null
        _isLoggedIn.value = false
    }

    suspend fun getCustomer(): Result<Customer> = authorizedRequest { client.get("customer/me") }
    suspend fun getEquipment(): Result<List<Equipment>> = authorizedRequest { client.get("customer/equipment") }
    suspend fun getEquipmentHistory(equipmentId: Int): Result<List<ServiceRecord>> = authorizedRequest { client.get("customer/equipment/$equipmentId/history") }
    suspend fun getServiceTypes(): Result<List<ServiceType>> = authorizedRequest { client.get("customer/appointments/service-types") }
    suspend fun getAppointments(): Result<List<Appointment>> = authorizedRequest { client.get("customer/appointments") }
    suspend fun createAppointment(request: CreateAppointmentRequest): Result<Appointment> = authorizedRequest {
        client.post("customer/appointments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
    suspend fun getInvoices(status: String = ""): Result<List<Invoice>> {
        val path = if (status.isBlank()) "customer/invoices" else "customer/invoices?status=$status"
        return authorizedRequest { client.get(path) }
    }
    suspend fun getInvoice(invoiceId: Int): Result<Invoice> = authorizedRequest { client.get("customer/invoices/$invoiceId") }
    suspend fun initiatePayment(invoiceId: Int): Result<PaymentInitiateResponse> = authorizedRequest {
        client.post("customer/invoices/$invoiceId/initiate-payment") {
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject { put("accept_card_fee", true) })
        }
    }
    suspend fun getPaymentStatus(invoiceId: Int): Result<PaymentStatusResponse> = authorizedRequest { client.get("customer/invoices/$invoiceId/payment-status") }
    suspend fun getWaterTests(): Result<WaterTestResponse> = authorizedRequest { client.get("customer/water-test") }
    suspend fun poll(): Result<PollResponse> = authorizedRequest { client.get("customer/poll") }
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = authorizedRequest {
        client.put("customer/password") {
            contentType(ContentType.Application.Json)
            setBody(ChangePasswordRequest(currentPassword, newPassword))
        }
    }
    suspend fun getVapidPublicKey(): Result<VapidKeyResponse> = authorizedRequest { client.get("customer/push/vapid-public-key") }
    suspend fun subscribePush(subscription: PushSubscription): Result<Unit> = authorizedRequest {
        client.post("customer/push/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(subscription)
        }
    }
    suspend fun unsubscribePush(): Result<Unit> = authorizedRequest { client.delete("customer/push/unsubscribe") }
    suspend fun requestSaltDelivery(request: SaltDeliveryRequest): Result<Appointment> = authorizedRequest {
        client.post("customer/salt-delivery") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
    suspend fun acknowledgeNotification(notificationId: Int): Result<Unit> = authorizedRequest {
        client.post("customer/notifications/$notificationId/ack")
    }

    private suspend inline fun <reified T> authorizedRequest(crossinline block: suspend () -> HttpResponse): Result<T> {
        return try {
            val response = withTimeout(35_000) { block() }
            val status = response.status.value
            if (status == 401 && !response.request.url.segments.any { it == "auth" }) {
                val refreshed = refreshAccessToken()
                if (refreshed) {
                    val retryResponse = withTimeout(35_000) { block() }
                    return handleResponse<T>(retryResponse)
                }
            }
            handleResponse<T>(response)
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Result.failure(ApiException(0, "Request timed out"))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        }
    }

    private suspend fun refreshAccessToken(): Boolean {
        val refreshToken = TokenRepository.refreshToken
        if (refreshToken.isBlank()) return false
        return try {
            val response = authClient.post("auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(refreshToken))
            }
            if (response.status.isSuccess()) {
                val refreshBody: RefreshResponse = response.body()
                TokenRepository.saveTokens(refreshBody.access_token, refreshBody.refresh_token, TokenRepository.customerId)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend inline fun <reified T> handleResponse(response: HttpResponse): Result<T> {
        return try {
            val bodyText = response.bodyAsText()
            if (response.status.isSuccess()) {
                if (bodyText.isBlank()) {
                    when (T::class) {
                        Unit::class -> Result.success(Unit as T)
                        PollResponse::class -> Result.success(PollResponse() as T)
                        List::class -> Result.success(emptyList<Any>() as T)
                        else -> Result.failure(ApiException(response.status.value, "Empty response body"))
                    }
                } else {
                    Result.success(jsonInstance.decodeFromString<T>(bodyText))
                }
            } else {
                val errorMsg = try {
                    jsonInstance.parseToJsonElement(bodyText).jsonObject["error"]?.jsonPrimitive?.content ?: "Error ${response.status.value}"
                } catch (_: Exception) { "Error ${response.status.value}" }
                Result.failure(ApiException(response.status.value, errorMsg))
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        }
    }
}

data class VapidKeyResponse(val key: String)
class ApiException(val statusCode: Int, message: String) : Exception(message)
