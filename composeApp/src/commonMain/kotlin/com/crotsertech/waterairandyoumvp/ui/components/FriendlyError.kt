package com.crotsertech.waterairandyoumvp.ui.components

fun friendlyErrorMessage(error: String?): String {
    if (error == null) return "Something went wrong. Please try again."
    val lower = error.lowercase()
    return when {
        lower.contains("no access token") || lower.contains("session expired") || lower.contains("401") || lower.contains("token") -> "Session timed out. Please sign in again."
        lower.contains("network") || lower.contains("timeout") || lower.contains("connect") || lower.contains("socket") || lower.contains("resolve") -> "Lost connection. Please check your internet."
        lower.contains("429") || lower.contains("too many") -> "Slow down a bit - please wait and try again."
        lower.contains("500") || lower.contains("server") || lower.contains("503") -> "Our server had an issue. Please try again shortly."
        lower.contains("404") || lower.contains("not found") -> "The requested information could not be found."
        lower.contains("invalid email") || lower.contains("invalid credentials") || lower.contains("login failed") -> "Invalid email or password. Please try again."
        lower.contains("password") && lower.contains("incorrect") -> "Current password is incorrect. Please try again."
        lower.contains("validation") || lower.contains("invalid") -> "Please check the information you entered."
        else -> error
    }
}
