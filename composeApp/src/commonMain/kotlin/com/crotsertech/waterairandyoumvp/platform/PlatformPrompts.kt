package com.crotsertech.waterairandyoumvp.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionRequester(): () -> Unit

@Composable
expect fun rememberUrlOpener(): (String) -> Unit

expect fun showLocalNotification(title: String, body: String)

expect fun getDeviceInfo(): String
