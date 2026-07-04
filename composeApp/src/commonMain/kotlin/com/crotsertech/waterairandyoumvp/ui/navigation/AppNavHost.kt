package com.crotsertech.waterairandyoumvp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.crotsertech.waterairandyoumvp.ui.screens.DashboardScreen
import com.crotsertech.waterairandyoumvp.ui.screens.EquipmentScreen
import com.crotsertech.waterairandyoumvp.ui.screens.AppointmentsScreen
import com.crotsertech.waterairandyoumvp.ui.screens.InvoicesScreen
import com.crotsertech.waterairandyoumvp.ui.screens.WaterScreen
import com.crotsertech.waterairandyoumvp.ui.screens.ProfileScreen
import com.crotsertech.waterairandyoumvp.ui.screens.LoginScreen

@Composable
fun NavGraphBuilder.appNavGraph(
    navController: androidx.navigation.NavController,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit
) {
    composable(route = Screen.Login.toString()) {
        LoginScreen(onLoginSuccess = onLoginSuccess)
    }
    
    navigation(
        startDestination = Screen.Dashboard().toString(),
        route = "main"
    ) {
        composable(route = Screen.Dashboard().toString()) {
            DashboardScreen(
                onNavigateToEquipment = { navController.navigate(Screen.Equipment().toString()) },
                onNavigateToAppointments = { navController.navigate(Screen.Appointments().toString()) },
                onNavigateToInvoices = { navController.navigate(Screen.Invoices().toString()) },
                onNavigateToWater = { navController.navigate(Screen.Water().toString()) },
                onLogout = onLogout
            )
        }
        composable(route = Screen.Equipment().toString()) { EquipmentScreen() }
        composable(route = Screen.Appointments().toString()) { AppointmentsScreen() }
        composable(route = Screen.Invoices().toString()) { InvoicesScreen() }
        composable(route = Screen.Water().toString()) { WaterScreen() }
        composable(route = Screen.Profile().toString()) { ProfileScreen(onLogout = onLogout) }
    }
}