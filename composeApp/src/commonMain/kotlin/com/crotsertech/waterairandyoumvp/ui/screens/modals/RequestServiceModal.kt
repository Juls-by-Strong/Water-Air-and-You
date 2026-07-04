package com.crotsertech.waterairandyoumvp.ui.screens.modals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.Equipment
import com.crotsertech.waterairandyoumvp.data.model.ServiceType
import com.crotsertech.waterairandyoumvp.data.model.CreateAppointmentRequest
import com.crotsertech.waterairandyoumvp.ui.components.ToastManager
import com.crotsertech.waterairandyoumvp.ui.components.ToastType
import com.crotsertech.waterairandyoumvp.ui.components.WayModal
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

@Composable
fun RequestServiceModal(
    visible: Boolean,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var serviceTypes by remember { mutableStateOf<List<ServiceType>>(emptyList()) }
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var selectedTypeId by remember { mutableStateOf<Int?>(null) }
    var selectedEquipmentIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var requestDate by remember { mutableStateOf("") }
    var timePref by remember { mutableStateOf("AM") }
    var saltDelivery by remember { mutableStateOf(false) }
    var oxyblast by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loadingData by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            loadingData = true
            var typesLoaded = false
            var equipLoaded = false
            ApiService.getServiceTypes().onSuccess { types ->
                serviceTypes = types
                typesLoaded = true
                if (equipLoaded) loadingData = false
            }.onFailure { loadingData = false }
            ApiService.getEquipment().onSuccess { equip ->
                equipmentList = equip
                equipLoaded = true
                if (typesLoaded) loadingData = false
            }.onFailure { loadingData = false }
        }
    }

    WayModal(
        visible = visible,
        onDismiss = onDismiss,
        title = "Request Service",
        subtitle = "Tell us what you need and when"
    ) {
        if (loadingData) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@WayModal
        }

        Text("Service Type", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box {
            OutlinedTextField(
                value = serviceTypes.find { it.type_id == selectedTypeId }?.name ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Select a service type") },
                trailingIcon = { Text(if (dropdownExpanded) "\u25B2" else "\u25BC", fontSize = 12.sp) },
                singleLine = true
            )
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                serviceTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name, fontSize = 14.sp) },
                        onClick = { selectedTypeId = type.type_id; dropdownExpanded = false }
                    )
                }
            }
            Box(Modifier.matchParentSize().padding(end = 40.dp)) {
                TextButton(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier.fillMaxSize()
                ) { }
            }
        }

        Text("Preferred Date", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = requestDate,
            onValueChange = { requestDate = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("YYYY-MM-DD") },
            singleLine = true
        )

        Text("Time Preference", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = timePref == "AM", label = { Text("Morning (AM)", fontSize = 12.sp) }, onClick = { timePref = "AM" })
            FilterChip(selected = timePref == "PM", label = { Text("Afternoon (PM)", fontSize = 12.sp) }, onClick = { timePref = "PM" })
            FilterChip(selected = timePref == "Open", label = { Text("Open", fontSize = 12.sp) }, onClick = { timePref = "Open" })
        }

        Text("Equipment", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        equipmentList.forEach { equip ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = equip.equipment_id in selectedEquipmentIds,
                    onCheckedChange = { checked ->
                        selectedEquipmentIds = if (checked) {
                            selectedEquipmentIds + equip.equipment_id
                        } else {
                            selectedEquipmentIds - equip.equipment_id
                        }
                    }
                )
                Text(equip.icon, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(equip.type_name, fontSize = 14.sp)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text("Add-Ons", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = saltDelivery, onCheckedChange = { saltDelivery = it })
            Text("Salt delivery", fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = oxyblast, onCheckedChange = { oxyblast = it })
            Text("OxyBlast treatment", fontSize = 14.sp)
        }

        Text("Notes", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            placeholder = { Text("Anything we should know?") }
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val typeId = selectedTypeId ?: return@Button
                if (requestDate.isBlank()) return@Button
                isLoading = true
                scope.launch {
                    val result = ApiService.createAppointment(
                        CreateAppointmentRequest(
                            service_type_id = typeId,
                            requested_date = requestDate,
                            requested_window = timePref,
                            customer_notes = notes.ifBlank { null },
                            equipment_ids = selectedEquipmentIds.toList(),
                            salt_delivery = saltDelivery,
                            oxyblast = oxyblast
                        )
                    )
                    isLoading = false
                    result.onSuccess {
                        ToastManager.show("Service request submitted!", ToastType.Success)
                        onSuccess()
                        onDismiss()
                    }.onFailure { e ->
                        ToastManager.show(e.message ?: "Failed to submit request", ToastType.Error)
                    }
                }
            },
            enabled = selectedTypeId != null && requestDate.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            } else {
                Text("Submit Request", fontWeight = FontWeight.Medium)
            }
        }
    }
}
