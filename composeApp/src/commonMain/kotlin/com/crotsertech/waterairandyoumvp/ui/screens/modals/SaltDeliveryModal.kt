package com.crotsertech.waterairandyoumvp.ui.screens.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.SaltDeliveryRequest
import com.crotsertech.waterairandyoumvp.ui.components.ToastManager
import com.crotsertech.waterairandyoumvp.ui.components.ToastType
import com.crotsertech.waterairandyoumvp.ui.components.WayModal
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

@Composable
fun SaltDeliveryModal(
    visible: Boolean,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var bagCount by remember { mutableStateOf(4) }
    var requestDate by remember { mutableStateOf("") }
    var timePref by remember { mutableStateOf("AM") }
    var notes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    WayModal(
        visible = visible,
        onDismiss = onDismiss,
        title = "Order Salt Delivery",
        subtitle = "Schedule salt delivery to your home"
    ) {
        Text("Number of Bags", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepperButton(text = "−", enabled = bagCount > 4, onClick = { bagCount-- })
            Text(
                text = "$bagCount",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(48.dp)
            )
            StepperButton(text = "+", enabled = bagCount < 100, onClick = { bagCount++ })
            Text(
                text = "× \$10 ea. = \$${bagCount * 10}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text("Delivery Date", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = requestDate,
            onValueChange = { requestDate = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("YYYY-MM-DD (minimum 4 days out)") },
            singleLine = true
        )

        Text("Time Preference", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(selected = timePref == "AM", label = { Text("Morning (AM)", fontSize = 12.sp) }, onClick = { timePref = "AM" })
            FilterChip(selected = timePref == "PM", label = { Text("Afternoon (PM)", fontSize = 12.sp) }, onClick = { timePref = "PM" })
            FilterChip(selected = timePref == "Open", label = { Text("Open", fontSize = 12.sp) }, onClick = { timePref = "Open" })
        }

        Text("Notes", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            placeholder = { Text("Special instructions?") }
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (requestDate.isBlank()) return@Button
                isLoading = true
                scope.launch {
                    val result = ApiService.requestSaltDelivery(
                        SaltDeliveryRequest(
                            bags = bagCount,
                            requested_date = requestDate,
                            requested_window = timePref,
                            notes = notes.ifBlank { null }
                        )
                    )
                    isLoading = false
                    result.onSuccess {
                        ToastManager.show("Salt delivery ordered!", ToastType.Success)
                        onSuccess()
                        onDismiss()
                    }.onFailure { e ->
                        ToastManager.show(e.message ?: "Failed to order salt delivery", ToastType.Error)
                    }
                }
            },
            enabled = requestDate.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            } else {
                Text("Order Delivery", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun StepperButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}
