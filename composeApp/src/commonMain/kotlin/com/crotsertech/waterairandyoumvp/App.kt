package com.crotsertech.waterairandyoumvp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.api.TokenRepository
import com.crotsertech.waterairandyoumvp.notification.NotificationScheduler
import com.crotsertech.waterairandyoumvp.data.model.Equipment
import com.crotsertech.waterairandyoumvp.platform.rememberNotificationPermissionRequester
import com.crotsertech.waterairandyoumvp.platform.rememberUrlOpener
import com.crotsertech.waterairandyoumvp.theme.WayTheme
import com.crotsertech.waterairandyoumvp.ui.components.ToastHost
import com.crotsertech.waterairandyoumvp.ui.navigation.BottomNavBar
import com.crotsertech.waterairandyoumvp.ui.navigation.Screen
import com.crotsertech.waterairandyoumvp.ui.navigation.mainScreens
import com.crotsertech.waterairandyoumvp.ui.screens.AppointmentsScreen
import com.crotsertech.waterairandyoumvp.ui.screens.DashboardScreen
import com.crotsertech.waterairandyoumvp.ui.screens.EquipmentScreen
import com.crotsertech.waterairandyoumvp.ui.screens.InvoicesScreen
import com.crotsertech.waterairandyoumvp.ui.screens.LoginScreen
import com.crotsertech.waterairandyoumvp.ui.screens.ProfileScreen
import com.crotsertech.waterairandyoumvp.ui.screens.WaterScreen
import com.crotsertech.waterairandyoumvp.ui.screens.modals.InvoiceDetailModal
import com.crotsertech.waterairandyoumvp.ui.screens.modals.RequestServiceModal
import com.crotsertech.waterairandyoumvp.ui.screens.modals.SaltDeliveryModal
import com.crotsertech.waterairandyoumvp.ui.screens.modals.ServiceHistoryModal
import com.crotsertech.waterairandyoumvp.ui.screens.modals.WelcomeModal
import kotlinx.coroutines.delay

@Composable
fun App() {
    val requestPermission = rememberNotificationPermissionRequester()

    LaunchedEffect(Unit) {
        if (!TokenRepository.hasCompletedFirstRun) {
            requestPermission()
            TokenRepository.hasCompletedFirstRun = true
        }
    }

    var useMetro by remember { mutableStateOf(TokenRepository.useMetro) }
    var useDark by remember { mutableStateOf(TokenRepository.useDark) }
    var showWelcome by remember { mutableStateOf(false) }

    WayTheme(
        useMetro = useMetro,
        useDark = useDark,
        onThemeChanged = { m, d -> useMetro = m; useDark = d }
    ) {
        val navController = rememberNavController()
        val isLoggedIn by ApiService.isLoggedIn.collectAsState()

        LaunchedEffect(useMetro, useDark) {
            TokenRepository.useMetro = useMetro
            TokenRepository.useDark = useDark
        }

        LaunchedEffect(isLoggedIn) {
            if (isLoggedIn) {
                NotificationScheduler.start()
            } else {
                NotificationScheduler.stop()
            }
        }

        val isMetro = WayTheme.colors.isMetro
        val isDark = WayTheme.colors.isDark

        if (isMetro) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = {
                        showWelcome = !TokenRepository.hasCompletedFirstRun
                    })
                } else {
                    Box(Modifier.fillMaxSize()) {
                        MainNavBar(navController = navController, onLogout = { ApiService.logout() })
                        ToastHost()
                        WelcomeModal(
                            visible = showWelcome,
                            onDismiss = { showWelcome = false },
                            onStartTour = {}
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDark) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0E0E28),
                                    Color(0xFF080818),
                                    Color(0xFF040410)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFC8F0FF),
                                    Color(0xFF8CD8F0),
                                    Color(0xFFA8EAC8)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        }
                    )
            ) {
                if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = {
                        showWelcome = !TokenRepository.hasCompletedFirstRun
                    })
                } else {
                    Box(Modifier.fillMaxSize()) {
                        MainNavBar(navController = navController, onLogout = { ApiService.logout() })
                        ToastHost()
                        WelcomeModal(
                            visible = showWelcome,
                            onDismiss = { showWelcome = false },
                            onStartTour = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainNavBar(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val openUrl = rememberUrlOpener()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ""
    var unpaidCount by remember { mutableStateOf(0) }
    var showRequestService by remember { mutableStateOf(false) }
    var showSaltDelivery by remember { mutableStateOf(false) }
    var invoiceDetailId by remember { mutableStateOf<Int?>(null) }
    var equipmentForHistory by remember { mutableStateOf<Equipment?>(null) }
    var paymentUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val onContactUs = remember { { openUrl("tel:3096431342") } }

    LaunchedEffect(Unit) {
        while (true) {
            ApiService.poll().onSuccess { poll ->
                unpaidCount = poll.unpaidCount
            }.onFailure {
                // If poll fails with 401, ApiService.isLoggedIn will change and App() will redirect to Login
            }
            delay(120_000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.weight(1f)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    onNavigateToEquipment = { navController.navigate("equipment") },
                    onNavigateToAppointments = { navController.navigate("appointments") },
                    onNavigateToInvoices = { navController.navigate("invoices") },
                    onNavigateToWater = { navController.navigate("water") },
                    onRequestService = { showRequestService = true },
                    onSaltDelivery = { showSaltDelivery = true },
                    onContactUs = onContactUs,
                    onServiceHistory = { equipmentForHistory = it },
                    onLogout = onLogout
                )
            }
            composable("equipment") { EquipmentScreen(onServiceHistory = { equipmentForHistory = it }) }
            composable("appointments") { AppointmentsScreen() }
            composable("invoices") {
                InvoicesScreen(
                    onInvoiceClick = { invId -> invoiceDetailId = invId }
                )
            }
            composable("water") { WaterScreen() }
            composable("profile") { ProfileScreen(onLogout = onLogout) }
        }

        BottomNavBar(
            navController = navController,
            screens = mainScreens,
            selectedScreen = when {
                currentRoute.startsWith("dashboard") -> Screen.Dashboard()
                currentRoute.startsWith("equipment") -> Screen.Equipment()
                currentRoute.startsWith("appointments") -> Screen.Appointments()
                currentRoute.startsWith("invoices") -> Screen.Invoices()
                currentRoute.startsWith("water") -> Screen.Water()
                currentRoute.startsWith("profile") -> Screen.Profile()
                else -> Screen.Dashboard()
            },
            invoiceBadgeCount = unpaidCount
        )
    }

    RequestServiceModal(
        visible = showRequestService,
        scope = scope,
        onDismiss = { showRequestService = false },
        onSuccess = {}
    )

    SaltDeliveryModal(
        visible = showSaltDelivery,
        scope = scope,
        onDismiss = { showSaltDelivery = false },
        onSuccess = {}
    )

    InvoiceDetailModal(
        visible = invoiceDetailId != null,
        invoiceId = invoiceDetailId ?: 0,
        scope = scope,
        onDismiss = { invoiceDetailId = null },
        onPaymentInitiated = { url -> paymentUrl = url }
    )

    equipmentForHistory?.let { equip ->
        ServiceHistoryModal(
            visible = true,
            equipment = equip,
            onDismiss = { equipmentForHistory = null }
        )
    }
}
