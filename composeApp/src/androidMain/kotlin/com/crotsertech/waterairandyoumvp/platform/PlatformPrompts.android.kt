package com.crotsertech.waterairandyoumvp.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.crotsertech.waterairandyoumvp.AndroidApp

private const val CHANNEL_ID = "service_reminders"

@Composable
actual fun rememberNotificationPermissionRequester(): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    return remember {
        {
            if (Build.VERSION.SDK_INT >= 33) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
actual fun rememberUrlOpener(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }
}

actual fun showLocalNotification(title: String, body: String) {
    val context = AndroidApp.context
    val manager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= 26) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Service Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications about service due dates and appointment updates"
        }
        manager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    manager.notify(notificationId, notification)
}
