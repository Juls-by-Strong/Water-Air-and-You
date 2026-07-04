package com.crotsertech.waterairandyoumvp.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed interface Screen {
    data object Login : Screen
    data class Dashboard(val route: String = "dashboard") : Screen {
        override fun toString() = route
    }
    data class Equipment(val route: String = "equipment") : Screen {
        override fun toString() = route
    }
    data class Appointments(val route: String = "appointments") : Screen {
        override fun toString() = route
    }
    data class Invoices(val route: String = "invoices") : Screen {
        override fun toString() = route
    }
    data class Water(val route: String = "water") : Screen {
        override fun toString() = route
    }
    data class Profile(val route: String = "profile") : Screen {
        override fun toString() = route
    }
}

fun Screen.getLabel(): String = when (this) {
    is Screen.Dashboard -> "Home"
    is Screen.Equipment -> "Equipment"
    is Screen.Appointments -> "Visits"
    is Screen.Invoices -> "Invoices"
    is Screen.Water -> "Testing"
    is Screen.Profile -> "Settings"
    Screen.Login -> "Login"
}

fun Screen.getIcon(): String = when (this) {
    is Screen.Dashboard -> "🏠"
    is Screen.Equipment -> "💧"
    is Screen.Appointments -> "📅"
    is Screen.Invoices -> "🧾"
    is Screen.Water -> "🔬"
    is Screen.Profile -> "⚙️"
    Screen.Login -> "🏠"
}