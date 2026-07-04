package com.crotsertech.waterairandyoumvp.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.UIKit.UIApplication
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

@Composable
actual fun rememberNotificationPermissionRequester(): () -> Unit {
    return remember {
        {
            val center = UNUserNotificationCenter.currentNotificationCenter()
            center.requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            ) { granted: Boolean, error: NSError? ->
                println("Notification permission granted: $granted error: $error")
            }
        }
    }
}

@Composable
actual fun rememberUrlOpener(): (String) -> Unit {
    return remember {
        { url ->
            NSURL(string = url)?.let { UIApplication.sharedApplication.openURL(it, emptyMap<Any?, Any>(), null) }
        }
    }
}

actual fun showLocalNotification(title: String, body: String) {
    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.getNotificationSettingsWithCompletionHandler { settings ->
        if (settings?.authorizationStatus != UNAuthorizationStatusAuthorized &&
            settings?.authorizationStatus != UNAuthorizationStatusProvisional
        ) {
            return@getNotificationSettingsWithCompletionHandler
        }
        val content = UNMutableNotificationContent()
        content.setTitle(title)
        content.setBody(body)
        content.setSound(UNNotificationSound.defaultSound())
        val request = UNNotificationRequest.requestWithIdentifier(
            "notification_${NSUUID().UUIDString}",
            content,
            null
        )
        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Failed to show notification: $error")
            }
        }
    }
}
