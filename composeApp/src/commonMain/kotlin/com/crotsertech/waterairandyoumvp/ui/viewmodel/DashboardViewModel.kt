package com.crotsertech.waterairandyoumvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.PollResponse
import com.crotsertech.waterairandyoumvp.ui.components.friendlyErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val pollData: PollResponse, val groups: List<EquipmentGroup>, val customerName: String? = null) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var fetchJob: Job? = null

    init { loadData() }

    fun loadData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            fetchData()
        }
    }

    fun refresh() {
        loadData()
    }

    private suspend fun fetchData() = withContext(Dispatchers.Default) {
        coroutineScope {
            val pollDeferred = async { ApiService.poll() }
            val equipmentDeferred = async { ApiService.getEquipment() }
            val customerDeferred = async { ApiService.getCustomer() }

            val pollResult = pollDeferred.await()
            val equipmentResult = equipmentDeferred.await()
            val customerResult = customerDeferred.await()

            val errors = mutableListOf<String>()
            if (pollResult.isFailure) errors.add(friendlyErrorMessage(pollResult.exceptionOrNull()?.message))
            if (equipmentResult.isFailure) errors.add(friendlyErrorMessage(equipmentResult.exceptionOrNull()?.message))

            if (errors.isEmpty()) {
                _uiState.value = DashboardUiState.Success(
                    pollData = pollResult.getOrThrow(),
                    groups = EquipmentViewModel.groupEquipment(equipmentResult.getOrThrow()),
                    customerName = customerResult.getOrNull()?.first_name
                )
            } else {
                _uiState.value = DashboardUiState.Error(errors.joinToString("; "))
            }
        }
    }
}
