package com.crotsertech.waterairandyoumvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.WaterTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WaterUiState {
    object Loading : WaterUiState()
    data class Success(val tests: List<WaterTest>) : WaterUiState()
    data class Error(val message: String) : WaterUiState()
}

class WaterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<WaterUiState>(WaterUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadWaterTests()
    }

    fun loadWaterTests() {
        viewModelScope.launch {
            _uiState.value = WaterUiState.Loading
            val result = ApiService.getWaterTests()
            result.onSuccess { response ->
                val allTests = listOfNotNull(response.current) + response.history
                _uiState.value = WaterUiState.Success(allTests)
            }.onFailure { e ->
                _uiState.value = WaterUiState.Error(e.message ?: "Failed to load water tests")
            }
        }
    }
}
