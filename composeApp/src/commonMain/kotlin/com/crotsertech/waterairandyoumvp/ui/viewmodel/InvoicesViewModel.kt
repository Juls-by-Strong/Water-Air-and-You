package com.crotsertech.waterairandyoumvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.Invoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class InvoicesUiState {
    object Loading : InvoicesUiState()
    data class Success(val invoices: List<Invoice>) : InvoicesUiState()
    data class Error(val message: String) : InvoicesUiState()
}

class InvoicesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<InvoicesUiState>(InvoicesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _uiState.value = InvoicesUiState.Loading
            val result = ApiService.getInvoices()
            result.onSuccess { list ->
                _uiState.value = InvoicesUiState.Success(list)
            }.onFailure { e ->
                _uiState.value = InvoicesUiState.Error(e.message ?: "Failed to load invoices")
            }
        }
    }
}
