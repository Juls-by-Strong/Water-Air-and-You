package com.crotsertech.waterairandyoumvp.ui.screens.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.Equipment
import com.crotsertech.waterairandyoumvp.data.model.ServiceRecord
import com.crotsertech.waterairandyoumvp.ui.components.WayModal

@Composable
fun ServiceHistoryModal(
    visible: Boolean,
    equipment: Equipment,
    onDismiss: () -> Unit
) {
    var records by remember { mutableStateOf<List<ServiceRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(visible, equipment.equipment_id) {
        if (visible) {
            isLoading = true
            ApiService.getEquipmentHistory(equipment.equipment_id).onSuccess {
                records = it
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        }
    }

    WayModal(
        visible = visible,
        onDismiss = onDismiss,
        title = "${equipment.icon} ${equipment.type_name}",
        subtitle = "Service history records"
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (records.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("No service records found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            records.forEach { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(record.service_type, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(record.service_date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (!record.notes.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(record.notes, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (!record.technician.isNullOrBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text("By: ${record.technician}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (!record.next_service_due.isNullOrBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text("Next due: ${record.next_service_due}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
