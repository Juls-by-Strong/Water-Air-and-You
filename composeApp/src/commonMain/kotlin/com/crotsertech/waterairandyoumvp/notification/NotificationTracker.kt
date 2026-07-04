package com.crotsertech.waterairandyoumvp.notification

import com.russhwolf.settings.Settings

object NotificationTracker {
    private val settings = Settings()

    private fun key(id: String) = "notif_fired_$id"

    fun hasFired(id: String): Boolean = settings.getBoolean(key(id), false)

    fun markFired(id: String) = settings.putBoolean(key(id), true)

    fun getPreviousStatus(id: String): String? =
        settings.getString("prev_status_$id", "").ifBlank { null }

    fun setPreviousStatus(id: String, status: String) =
        settings.putString("prev_status_$id", status)
}
