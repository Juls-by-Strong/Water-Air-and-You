package com.crotsertech.waterairandyoumvp.ui.screens.modals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.theme.LocalThemeState
import com.crotsertech.waterairandyoumvp.theme.glow
import com.crotsertech.waterairandyoumvp.ui.components.WayModal

@Composable
fun WelcomeModal(
    visible: Boolean,
    onDismiss: () -> Unit,
    onStartTour: () -> Unit
) {
    val themeState = LocalThemeState.current
    var step by remember { mutableIntStateOf(0) }

    val steps = listOf(
        "Welcome" to "Welcome to Water Air & You! Track your service visits, equipment, invoices, and water quality reports all in one place.",
        "Theme" to "Choose your theme. SNEP has rounded corners and rich colors. METRO offers a clean, flat design. Toggle dark mode for night use.",
        "Features" to "View equipment status at a glance, schedule service requests, track invoices, and review water quality reports. Notifications keep you updated.",
        "Ready" to "Some features are still under development. If something doesn't work yet, check back soon!"
    )

    WayModal(
        visible = visible,
        onDismiss = {},
        title = if (step == 0) "Welcome!" else "Step ${step + 1} of ${steps.size}",
        subtitle = ""
    ) {
        Text(
            text = steps[step].second,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(8.dp))

        if (step == 1) {
            Text("Quick theme pick:", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeQuickButton("SNEP Light", isActive = !themeState.useMetro && !themeState.useDark, modifier = Modifier.weight(1f)) {
                    themeState.onThemeChanged(false, false)
                }
                ThemeQuickButton("METRO Light", isActive = themeState.useMetro && !themeState.useDark, modifier = Modifier.weight(1f)) {
                    themeState.onThemeChanged(true, false)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeQuickButton("SNEP Dark", isActive = !themeState.useMetro && themeState.useDark, modifier = Modifier.weight(1f)) {
                    themeState.onThemeChanged(false, true)
                }
                ThemeQuickButton("METRO Dark", isActive = themeState.useMetro && themeState.useDark, modifier = Modifier.weight(1f)) {
                    themeState.onThemeChanged(true, true)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 0) {
                Box(Modifier.padding(vertical = 4.dp).glow(), contentAlignment = Alignment.Center) {
                    OutlinedButton(onClick = { step-- }) {
                        Text("Back")
                    }
                }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            if (step < steps.size - 1) {
                Box(Modifier.padding(vertical = 4.dp).glow(), contentAlignment = Alignment.Center) {
                    Button(onClick = { step++ }) {
                        Text("Next")
                    }
                }
            } else {
                Box(Modifier.padding(vertical = 4.dp).glow(), contentAlignment = Alignment.Center) {
                    Button(onClick = {
                        onDismiss()
                        if (step >= 2) onStartTour()
                    }) {
                        Text("Get Started")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ThemeQuickButton(label: String, isActive: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (isActive) {
        FilledTonalButton(onClick = {}, modifier = modifier, shape = MaterialTheme.shapes.small) {
            Text(label, fontSize = 12.sp)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier, shape = MaterialTheme.shapes.small) {
            Text(label, fontSize = 12.sp)
        }
    }
}
