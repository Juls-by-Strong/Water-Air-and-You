package com.crotsertech.waterairandyoumvp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crotsertech.waterairandyoumvp.data.model.Equipment
import com.crotsertech.waterairandyoumvp.data.model.DueStatus
import com.crotsertech.waterairandyoumvp.platform.rememberUrlOpener
import com.crotsertech.waterairandyoumvp.ui.viewmodel.DashboardUiState
import com.crotsertech.waterairandyoumvp.ui.viewmodel.DashboardViewModel
import com.crotsertech.waterairandyoumvp.ui.viewmodel.EquipmentGroup

private enum class EqDueStatus(val label: String) {
    AllGood("All Good"),
    Attention("Attention"),
    DueNow("Due Now"),
    Overdue("Overdue")
}

private val Green = Color(0xFF34C759)
private val Yellow = Color(0xFFFF9F0A)
private val Red = Color(0xFFFF453A)
private val Black = Color(0xFF3A3A3C)

private fun closestEqDueStatus(equipment: List<Equipment>): EqDueStatus {
    val minDays = equipment.mapNotNull { it.days_until_due }.minOrNull()
    return when {
        minDays == null -> EqDueStatus.AllGood
        minDays < 0 -> EqDueStatus.Overdue
        minDays <= 14 -> EqDueStatus.DueNow
        minDays <= 90 -> EqDueStatus.Attention
        else -> EqDueStatus.AllGood
    }
}

private fun statusColor(status: EqDueStatus): Color = when (status) {
    EqDueStatus.AllGood -> Green
    EqDueStatus.Attention -> Yellow
    EqDueStatus.DueNow -> Red
    EqDueStatus.Overdue -> Black
}

@Composable
fun DashboardScreen(
    onNavigateToEquipment: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    onNavigateToWater: () -> Unit,
    onRequestService: () -> Unit = {},
    onSaltDelivery: () -> Unit = {},
    onContactUs: () -> Unit = {},
    onServiceHistory: (Equipment) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel { DashboardViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardUiState.Success -> {
                val allEquipment = state.groups.flatMap { listOf(it.parent) + it.subComponents }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    item { WelcomeHero(state.pollData, allEquipment, state.customerName) }
                    item { Spacer(Modifier.height(4.dp)) }
                    item { SectionLabel("At a glance") }
                    item { HealthTiles(state.pollData, allEquipment, onNavigateToEquipment, onNavigateToAppointments, onNavigateToInvoices, onNavigateToWater) }
                    item { Spacer(Modifier.height(4.dp)) }
                    item { SectionLabel("Quick actions") }
                    item {
                        QuickActions(
                            onAppointments = onNavigateToAppointments,
                            onInvoices = onNavigateToInvoices,
                            onWater = onNavigateToWater,
                            onRequestService = onRequestService,
                            onSaltDelivery = onSaltDelivery,
                            onContactUs = onContactUs
                        )
                    }
                    if (state.pollData.appointments.isNotEmpty()) {
                        item { Spacer(Modifier.height(4.dp)) }
                        item { SectionLabel("Upcoming") }
                        items(state.pollData.appointments) { appt ->
                            AppointmentCard(appt, onNavigateToAppointments)
                        }
                    }
                    val dueGroups = state.groups
                        .filter { group ->
                            group.parent.days_until_due != null ||
                            group.subComponents.any { it.days_until_due != null }
                        }
                        .filter { it.parent.type_name !in com.crotsertech.waterairandyoumvp.ui.viewmodel.EquipmentViewModel.subComponentNames }
                    if (dueGroups.isNotEmpty()) {
                        item { Spacer(Modifier.height(4.dp)) }
                        item { SectionLabel("Your equipment") }
                        items(dueGroups) { group ->
                            DashboardExpandableCard(group.parent, group.subComponents, onServiceHistory = { onServiceHistory(group.parent) })
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
            is DashboardUiState.Error -> {
                val openUrl = rememberUrlOpener()
                val isSessionExpired = state.message.contains("Session timed out", ignoreCase = true)
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    if (isSessionExpired) {
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Refresh Token")
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onLogout) {
                            Text("Log Out")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { openUrl("https://github.com/Juls-by-Strong/Water-Air-and-You/issues/new?labels=bug") }) {
                            Text("Report a Bug")
                        }
                    } else {
                        Button(onClick = { viewModel.loadData() }) {
                            Text("Retry")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { openUrl("https://github.com/Juls-by-Strong/Water-Air-and-You/issues/new?labels=bug") }) {
                            Text("Report a Bug")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight(600),
        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
    )
}

@Composable
private fun WelcomeHero(pollData: com.crotsertech.waterairandyoumvp.data.model.PollResponse, equipment: List<Equipment>, customerName: String? = null) {
    val colors = com.crotsertech.waterairandyoumvp.theme.WayTheme.colors
    val greeting = if (customerName != null) "Welcome, $customerName" else "Welcome"

    if (colors.isMetro) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(greeting, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(2.dp))
                Text("Water Air and You - We Bring Healthier Water and Air to You!", fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                val overdueCount = equipment.count { it.dueStatus == DueStatus.Overdue }
                val soonCount = equipment.count { it.dueStatus == DueStatus.Soon }
                if (overdueCount > 0 || soonCount > 0) {
                    Text(text = buildString {
                        if (overdueCount > 0) append("$overdueCount overdue")
                        if (overdueCount > 0 && soonCount > 0) append(", ")
                        if (soonCount > 0) append("$soonCount due soon")
                        append(" - tap to view")
                    }, fontSize = 12.sp, color = if (overdueCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Medium)
                }
            }
        }
    } else {
        // Glowing pill-shaped blue gel welcome banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(colors.radiusLg.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4DA6FF),
                            Color(0xFF007AFF),
                            Color(0xFF0055CC)
                        )
                    )
                )
                .drawBehind {
                    val inset = 1.dp.toPx()
                    val path = Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                left = inset, top = inset,
                                right = size.width - inset, bottom = size.height - inset,
                                radiusX = colors.radiusLg.dp.toPx() - 0.5.dp.toPx(),
                                radiusY = colors.radiusLg.dp.toPx() - 0.5.dp.toPx()
                            )
                        )
                    }
                    drawPath(path, color = Color.White.copy(alpha = 0.3f), style = Stroke(width = 1.dp.toPx()))
                }
        ) {
            // Gloss reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = colors.radiusLg.dp, topEnd = colors.radiusLg.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Column(Modifier.padding(20.dp)) {
                Text(greeting, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(2.dp))
                Text("Water Air and You - We Bring Healthier Water and Air to You!", fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f))
                Spacer(Modifier.height(6.dp))
                val overdueCount = equipment.count { it.dueStatus == DueStatus.Overdue }
                val soonCount = equipment.count { it.dueStatus == DueStatus.Soon }
                if (overdueCount > 0 || soonCount > 0) {
                    Text(text = buildString {
                        if (overdueCount > 0) append("$overdueCount overdue")
                        if (overdueCount > 0 && soonCount > 0) append(", ")
                        if (soonCount > 0) append("$soonCount due soon")
                        append(" - tap to view")
                    }, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun HealthTiles(
    pollData: com.crotsertech.waterairandyoumvp.data.model.PollResponse,
    equipment: List<Equipment>,
    onEquipment: () -> Unit,
    onAppointments: () -> Unit,
    onInvoices: () -> Unit,
    onWater: () -> Unit
) {
    val activeStatus = closestEqDueStatus(equipment)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EquipmentDueStatusCard(activeStatus, modifier = Modifier.weight(1f), onClick = onEquipment)
            HealthTile(
                label = "Next visit",
                value = pollData.appointments.firstOrNull()?.displayDate?.takeLast(5) ?: "-",
                sub = pollData.appointments.firstOrNull()?.let { it.displayTime.ifBlank { it.displayWindow.ifBlank { "Tap to view" } } } ?: "No upcoming visits",
                modifier = Modifier.weight(1f),
                onClick = onAppointments
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HealthTile(
                label = "Balance",
                value = if (pollData.unpaid_count > 0) "${pollData.unpaid_count} ${if (pollData.unpaid_count == 1) "invoice" else "invoices"}" else "None",
                sub = if (pollData.unpaid_count > 0) "Tap to view" else "All paid",
                modifier = Modifier.weight(1f),
                onClick = onInvoices,
                valueColor = if (pollData.unpaid_count > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            HealthTile(
                label = "Water",
                value = "Testing",
                sub = "Tap to view",
                modifier = Modifier.weight(1f),
                onClick = onWater
            )
        }
    }
}

@Composable
private fun cardElevation() = if (com.crotsertech.waterairandyoumvp.theme.WayTheme.colors.isMetro) {
    CardDefaults.cardElevation(defaultElevation = 0.dp)
} else {
    CardDefaults.cardElevation(defaultElevation = 4.dp)
}

@Composable
private fun EquipmentDueStatusCard(activeStatus: EqDueStatus, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val color = statusColor(activeStatus)
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = cardElevation()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("EQUIPMENT", fontSize = 10.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = Color.White.copy(alpha = 0.85f))
            Spacer(Modifier.height(2.dp))
            Text(activeStatus.label, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun HealthTile(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        elevation = cardElevation()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label.uppercase(), fontSize = 10.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Text(sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickActions(
    onAppointments: () -> Unit,
    onInvoices: () -> Unit,
    onWater: () -> Unit,
    onRequestService: () -> Unit,
    onSaltDelivery: () -> Unit,
    onContactUs: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickAction(icon = "🔧", text = "Request service", modifier = Modifier.weight(1f), onClick = onRequestService)
            QuickAction(icon = "💳", text = "Pay an invoice", modifier = Modifier.weight(1f), onClick = onInvoices)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickAction(icon = "📅", text = "Service history", modifier = Modifier.weight(1f), onClick = onAppointments)
            QuickAction(icon = "💧", text = "Water reports", modifier = Modifier.weight(1f), onClick = onWater)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickAction(icon = "☎", text = "Contact us", modifier = Modifier.weight(1f), onClick = onContactUs)
            QuickAction(icon = "🧂", text = "Salt delivery", modifier = Modifier.weight(1f), onClick = onSaltDelivery)
        }
    }
}

@Composable
private fun QuickAction(icon: String, text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)),
        elevation = cardElevation()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text(text, fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun AppointmentCard(
    appt: com.crotsertech.waterairandyoumvp.data.model.Appointment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onClick),
        elevation = cardElevation()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(appt.service_type, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text("Date: ${appt.displayDate}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (appt.displayTime.isNotBlank()) {
                Text("Time: ${appt.displayTime}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            StatusBadge(appt.statusDisplay, appt.status)
        }
    }
}

@Composable
private fun DashboardExpandableCard(equip: Equipment, subComponents: List<Equipment> = emptyList(), onServiceHistory: () -> Unit = {}) {
    var expanded by remember { mutableStateOf(false) }
    val isMetro = com.crotsertech.waterairandyoumvp.theme.WayTheme.colors.isMetro
    val enter = if (isMetro) EnterTransition.None else fadeIn(animationSpec = tween(250)) + expandVertically(animationSpec = tween(250))
    val exit = if (isMetro) ExitTransition.None else fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))

    val allItems = remember(equip, subComponents) { listOf(equip) + subComponents }

    val combinedDaysUntilDue = remember(allItems) {
        allItems.mapNotNull { it.days_until_due }.minOrNull()
    }
    val combinedDueStatus = remember(combinedDaysUntilDue) {
        when {
            combinedDaysUntilDue == null -> DueStatus.Unknown
            combinedDaysUntilDue < 0 -> DueStatus.Overdue
            combinedDaysUntilDue <= 30 -> DueStatus.Soon
            else -> DueStatus.Ok
        }
    }
    val combinedLastService = remember(allItems) {
        allItems.mapNotNull { it.last_service_date }.maxOrNull()
    }
    val combinedNextServiceDue = remember(allItems) {
        allItems.mapNotNull { it.next_service_due }.minOrNull()
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = cardElevation()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(16.dp, 14.dp, 16.dp, 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    Text(equip.icon, fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(equip.type_name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                    if (equip.modelDisplay.isNotBlank()) {
                        Text(equip.modelDisplay, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
                if (combinedDueStatus != DueStatus.Unknown) {
                    StatusBadge(combinedDueStatus.name, combinedDueStatus)
                }
                Spacer(Modifier.width(6.dp))
                Text(if (expanded) "\u25B2" else "\u25BC", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            CountdownBar(combinedDueStatus, combinedDaysUntilDue)

            AnimatedVisibility(visible = expanded, enter = enter, exit = exit) {
                Column {
                    HorizontalDivider()
                    ThreeDateRow(combinedLastService, combinedNextServiceDue, combinedDaysUntilDue)
                    if (subComponents.isNotEmpty()) {
                        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                        SubComponentSection(subComponents)
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(onClick = onServiceHistory) {
                            Text("Service History", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownBar(dueStatus: DueStatus, daysUntilDue: Int?) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val (fillColor, fillWidth) = when (dueStatus) {
        DueStatus.Overdue -> MaterialTheme.colorScheme.error to 1f
        DueStatus.Soon -> MaterialTheme.colorScheme.tertiary to 0.5f
        DueStatus.Ok -> MaterialTheme.colorScheme.primary to 0.15f
        DueStatus.Unknown -> MaterialTheme.colorScheme.onSurfaceVariant to 0f
    }
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(6.dp)
            .clip(RoundedCornerShape(3.dp)).background(trackColor)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(fillWidth).fillMaxHeight()
                .clip(RoundedCornerShape(3.dp)).background(fillColor)
        )
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun ThreeDateRow(lastService: String?, nextDue: String?, daysUntilDue: Int?) {
    Column(Modifier.padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            DateCol("Last service", lastService ?: "\u2014")
            DateCol("Next due", nextDue ?: "\u2014")
            DateCol("Overdue", if (daysUntilDue != null && daysUntilDue < 0) "${-daysUntilDue} days" else "\u2014")
        }
    }
}

@Composable
private fun DateCol(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), fontSize = 10.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight(600))
    }
}

@Composable
private fun StatusBadge(label: String, raw: Any) {
    if (raw == DueStatus.Unknown) return
    val color = when {
        raw == DueStatus.Overdue || raw.toString().contains("overdue", ignoreCase = true) -> MaterialTheme.colorScheme.error
        raw == DueStatus.Soon || raw.toString().contains("soon", ignoreCase = true) || raw.toString().contains("pending", ignoreCase = true) -> MaterialTheme.colorScheme.tertiary
        raw == DueStatus.Ok || raw.toString().contains("completed", ignoreCase = true) || raw.toString().contains("paid", ignoreCase = true) -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = MaterialTheme.shapes.small,
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

@Composable
private fun SubComponentSection(subComponents: List<Equipment>) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
        Text(
            "COMPONENTS",
            fontSize = 10.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight(600),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        subComponents.forEach { sub ->
            SubRow(sub)
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun SubRow(sub: Equipment) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(sub.icon, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(sub.type_name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (sub.modelDisplay.isNotBlank()) {
                Text(sub.modelDisplay, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            if (sub.next_service_due != null) {
                Text(sub.next_service_due, fontSize = 11.sp, fontWeight = FontWeight(600))
            }
            if (sub.days_until_due != null) {
                val color = when {
                    sub.days_until_due < 0 -> MaterialTheme.colorScheme.error
                    sub.days_until_due <= 30 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
                val text = if (sub.days_until_due < 0) "OVERDUE" else sub.days_until_due.toString()
                Text(text, fontSize = 10.sp, fontWeight = FontWeight(700), color = color)
            }
        }
    }
}
