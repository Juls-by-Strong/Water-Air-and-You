package com.crotsertech.waterairandyoumvp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.Equipment
import com.crotsertech.waterairandyoumvp.data.model.ServiceRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EquipmentGroup(
    val parent: Equipment,
    val subComponents: List<Equipment>
)

sealed class EquipmentUiState {
    object Loading : EquipmentUiState()
    data class Success(val groups: List<EquipmentGroup>, val expandedId: Int? = null) : EquipmentUiState()
    data class Error(val message: String) : EquipmentUiState()
}

class EquipmentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<EquipmentUiState>(EquipmentUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadEquipment()
    }

    fun loadEquipment() {
        viewModelScope.launch {
            _uiState.value = EquipmentUiState.Loading
            val result = ApiService.getEquipment()
            result.onSuccess { list ->
                _uiState.value = EquipmentUiState.Success(groupEquipment(list))
            }.onFailure { e ->
                _uiState.value = EquipmentUiState.Error(e.message ?: "Failed to load equipment")
            }
        }
    }

    companion object {
        // All known sub-component type names (for filtering on dashboard)
        val subComponentNames: Set<String> = setOf(
            "Reverse Osmosis Filters", "Reverse Osmosis Membrane",
            "Pump Tube"
        )

        private val subComponentParentMap = mapOf(
            "Reverse Osmosis Filters" to "Reverse Osmosis System",
            "Reverse Osmosis Membrane" to "Reverse Osmosis System",
            "Pump Tube" to "Chemical Pump"
        )

        fun groupEquipment(equipment: List<Equipment>): List<EquipmentGroup> {
            val subComponents = equipment.filter { it.type_name in subComponentParentMap }
            val nonSubComponents = equipment.filter { it.type_name !in subComponentParentMap }

            val subByParentType = subComponents.groupBy {
                subComponentParentMap[it.type_name] ?: ""
            }

            return nonSubComponents.map { equip ->
                EquipmentGroup(
                    parent = equip,
                    subComponents = subByParentType[equip.type_name] ?: emptyList()
                )
            }.sortedBy { it.parent.days_until_due ?: Int.MAX_VALUE }
        }
    }
}
