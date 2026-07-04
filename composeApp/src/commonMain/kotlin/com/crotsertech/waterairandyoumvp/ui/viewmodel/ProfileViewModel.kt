package com.crotsertech.waterairandyoumvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.api.TokenRepository
import com.crotsertech.waterairandyoumvp.data.model.Customer
import com.crotsertech.waterairandyoumvp.data.model.PushSubscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val customer: Customer) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _passwordResult = MutableStateFlow<String?>(null)
    val passwordResult = _passwordResult.asStateFlow()

    private val _pushSubscribed = MutableStateFlow(false)
    val pushSubscribed = _pushSubscribed.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = ApiService.getCustomer()
            result.onSuccess { customer ->
                _uiState.value = ProfileUiState.Success(customer)
            }.onFailure { e ->
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun changePassword(current: String, newPw: String) {
        viewModelScope.launch {
            _passwordResult.value = null
            val result = ApiService.changePassword(current, newPw)
            result.onSuccess {
                _passwordResult.value = "Password changed successfully"
            }.onFailure { e ->
                _passwordResult.value = e.message ?: "Failed to change password"
            }
        }
    }

    fun clearPasswordResult() {
        _passwordResult.value = null
    }

    fun togglePushSubscription(subscribe: Boolean) {
        viewModelScope.launch {
            if (subscribe) {
                ApiService.subscribePush(
                    PushSubscription(
                        endpoint = "app-endpoint",
                        p256dh = null,
                        auth = null
                    )
                ).onSuccess {
                    _pushSubscribed.value = true
                }
            } else {
                ApiService.unsubscribePush().onSuccess {
                    _pushSubscribed.value = false
                }
            }
        }
    }

    fun logout() {
        TokenRepository.clearTokens()
    }
}
