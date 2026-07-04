package com.crotsertech.waterairandyoumvp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.crotsertech.waterairandyoumvp.theme.WayTheme

@Composable
fun BottomNavBar(
    navController: NavController,
    screens: List<Screen>,
    selectedScreen: Screen,
    invoiceBadgeCount: Int = 0
) {
    val colors = WayTheme.colors
    val isSnep = !colors.isMetro

    if (isSnep) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top reflection line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
                    .align(Alignment.TopCenter)
            )

            NavigationBar(
                containerColor = if (colors.isDark) {
                    Color(0xCC0A0A1A)
                } else {
                    Color(0xCCE8F4FF)
                },
                tonalElevation = 0.dp,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                screens.forEach { screen ->
                    val selected = screen == selectedScreen
                    val badgeCount = if (screen is Screen.Invoices) invoiceBadgeCount else 0

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.toString()) {
                                popUpTo(navController.graph.startDestinationRoute ?: "dashboard") {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ) {
                                            Text(
                                                text = if (badgeCount > 99) "99+" else "$badgeCount",
                                                fontSize = 9.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = screen.getIcon(),
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = screen.getLabel(),
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                }
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            selectedIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    } else {
        NavigationBar(
            containerColor = if (colors.isDark) Color(0xFF000000) else Color(0xFFFFFFFF),
            tonalElevation = 0.dp,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            screens.forEach { screen ->
                val selected = screen == selectedScreen
                val badgeCount = if (screen is Screen.Invoices) invoiceBadgeCount else 0

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.toString()) {
                            popUpTo(navController.graph.startDestinationRoute ?: "dashboard") {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (badgeCount > 0) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text(
                                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                                            fontSize = 9.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = screen.getIcon(),
                                fontSize = 24.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = screen.getLabel(),
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

val mainScreens = listOf(
    Screen.Dashboard(),
    Screen.Equipment(),
    Screen.Appointments(),
    Screen.Invoices(),
    Screen.Water(),
    Screen.Profile()
)
