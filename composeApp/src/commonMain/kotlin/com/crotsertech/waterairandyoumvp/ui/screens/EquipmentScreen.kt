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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crotsertech.waterairandyoumvp.data.model.Equipment
import com.crotsertech.waterairandyoumvp.data.model.DueStatus
import com.crotsertech.waterairandyoumvp.ui.viewmodel.EquipmentGroup
import com.crotsertech.waterairandyoumvp.ui.viewmodel.EquipmentUiState
import com.crotsertech.waterairandyoumvp.ui.viewmodel.EquipmentViewModel

@Composable
fun EquipmentScreen(
    onServiceHistory: (Equipment) -> Unit = {},
    viewModel: EquipmentViewModel = viewModel { EquipmentViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Your equipment",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Installed systems & service status",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        when (val state = uiState) {
            is EquipmentUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is EquipmentUiState.Success -> {
                if (state.groups.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No equipment found")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.groups) { group ->
                            EquipmentExpandableCard(
                                equip = group.parent,
                                subComponents = group.subComponents,
                                onServiceHistory = { onServiceHistory(group.parent) }
                            )
                        }
                    }
                }
            }
            is EquipmentUiState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadEquipment() }) { Text("Retry") }
                }
            }
        }
    }
}

@Composable
private fun EquipmentExpandableCard(equip: Equipment, subComponents: List<Equipment> = emptyList(), onServiceHistory: () -> Unit = {}) {
    var expanded by remember { mutableStateOf(false) }
    val isMetro = com.crotsertech.waterairandyoumvp.theme.WayTheme.colors.isMetro
    val enter = if (isMetro) EnterTransition.None else fadeIn(animationSpec = tween(250)) + expandVertically(animationSpec = tween(250))
    val exit = if (isMetro) ExitTransition.None else fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(16.dp, 14.dp, 16.dp, 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(equip.icon, fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(equip.type_name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                    Text(
                        if (equip.modelDisplay.isNotBlank()) equip.modelDisplay else equip.status ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(if (expanded) "\u25B2" else "\u25BC", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            CountdownBar(equip.dueStatus, equip.days_until_due)

            AnimatedVisibility(visible = expanded, enter = enter, exit = exit) {
                Column {
                    HorizontalDivider()
                    ThreeDateRow(equip.last_service_date, equip.next_service_due, equip.days_until_due)
                    if (subComponents.isNotEmpty()) {
                        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                        SubComponentSection(subComponents)
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onServiceHistory,
                            modifier = Modifier.weight(1f)
                        ) {
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
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DateCol("Last service", lastService ?: "\u2014")
            DateCol("Next due", nextDue ?: "\u2014")
            DateCol("Overdue", if (daysUntilDue != null && daysUntilDue < 0) "${-daysUntilDue} days" else "\u2014")
        }
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

@Composable
private fun DateCol(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label.uppercase(),
            fontSize = 10.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight(600),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight(600))
    }
}
