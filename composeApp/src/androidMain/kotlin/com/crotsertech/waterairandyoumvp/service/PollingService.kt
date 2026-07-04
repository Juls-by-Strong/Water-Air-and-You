package com.crotsertech.waterairandyoumvp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.crotsertech.waterairandyoumvp.notification.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PollingService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        startPolling()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel("PollingService destroyed")
        NotificationScheduler.stop()
        super.onDestroy()
    }

    private fun createChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Background Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Checking for service reminders in the background"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildForegroundNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        return builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Water Air and You")
            .setContentText("Checking for service reminders")
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun startPolling() {
        serviceScope.launch {
            NotificationScheduler.start()
        }
    }

    companion object {
        const val CHANNEL_ID = "polling_service"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP = "com.crotsertech.waterairandyoumvp.STOP_POLLING"
    }
}
