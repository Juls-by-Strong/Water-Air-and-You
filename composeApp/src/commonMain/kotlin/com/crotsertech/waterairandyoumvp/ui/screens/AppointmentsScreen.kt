package com.crotsertech.waterairandyoumvp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crotsertech.waterairandyoumvp.ui.viewmodel.AppointmentsUiState
import com.crotsertech.waterairandyoumvp.ui.viewmodel.AppointmentsViewModel

@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel = viewModel { AppointmentsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your visits",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Service history & what's coming up",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        when (val state = uiState) {
            is AppointmentsUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AppointmentsUiState.Success -> {
                if (state.appointments.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No appointments found")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.appointments) { item ->
                            AppointmentCard(item)
                        }
                    }
                }
            }
            is AppointmentsUiState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadAppointments() }) { Text("Retry") }
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(appt: com.crotsertech.waterairandyoumvp.data.model.Appointment) {
    val accentColor = when (appt.status) {
        "pending" -> MaterialTheme.colorScheme.tertiary
        "confirmed" -> MaterialTheme.colorScheme.primary
        "in_progress" -> MaterialTheme.colorScheme.secondary
        "completed" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier.width(4.dp).fillMaxHeight().defaultMinSize(minHeight = 80.dp)
                    .clip(RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp))
                    .background(accentColor)
            )
            Column(
                Modifier.padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(appt.service_type, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    StatusBadge(appt.statusDisplay, appt.status)
                }
                Spacer(Modifier.height(6.dp))
                Text("Date: ${appt.displayDate}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (appt.displayTime.isNotBlank()) {
                    Text("Time: ${appt.displayTime}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (appt.displayWindow.isNotBlank()) {
                    Text("Window: ${appt.displayWindow}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, rawStatus: String) {
    val color = when {
        rawStatus.contains("overdue", ignoreCase = true) || rawStatus == "overdue" -> MaterialTheme.colorScheme.error
        rawStatus == "pending" || rawStatus.contains("soon", ignoreCase = true) -> MaterialTheme.colorScheme.tertiary
        rawStatus == "confirmed" || rawStatus == "sent" || rawStatus.contains("progress", ignoreCase = true) -> MaterialTheme.colorScheme.primary
        rawStatus == "completed" || rawStatus == "paid" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(20),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight(700),
            letterSpacing = 0.3.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
