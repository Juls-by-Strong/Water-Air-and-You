package com.crotsertech.waterairandyoumvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppointmentsUiState {
    object Loading : AppointmentsUiState()
    data class Success(val appointments: List<Appointment>) : AppointmentsUiState()
    data class Error(val message: String) : AppointmentsUiState()
}

class AppointmentsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AppointmentsUiState>(AppointmentsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.value = AppointmentsUiState.Loading
            val result = ApiService.getAppointments()
            result.onSuccess { list ->
                _uiState.value = AppointmentsUiState.Success(list)
            }.onFailure { e ->
                _uiState.value = AppointmentsUiState.Error(e.message ?: "Failed to load appointments")
            }
        }
    }
}
