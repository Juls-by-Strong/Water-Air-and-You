package com.crotsertech.waterairandyoumvp.notification

import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.model.Appointment
import com.crotsertech.waterairandyoumvp.data.model.Equipment
import com.crotsertech.waterairandyoumvp.platform.showLocalNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

object NotificationScheduler {
    private const val POLL_INTERVAL_MS = 60_000L

    private var scope: CoroutineScope? = null

    val isRunning: Boolean get() = scope?.isActive == true

    fun start() {
        if (isRunning) return
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        scope?.launch {
            delay(15_000)
            while (isActive) {
                try {
                    pollAndNotify()
                } catch (_: Exception) {
                }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    fun stop() {
        scope?.cancel("NotificationScheduler stopped")
        scope = null
    }

    suspend fun pollAndNotify() {
        val poll = ApiService.poll().getOrNull() ?: return
        val equipment = ApiService.getEquipment().getOrNull() ?: emptyList()

        checkPendingNotifications(poll.notifications)
        checkEquipmentDueDates(equipment)
        checkAppointments(poll.appointments)
        checkInvoices()
    }

    private suspend fun checkPendingNotifications(notifications: List<com.crotsertech.waterairandyoumvp.data.model.PendingNotification>) {
        for (n in notifications) {
            if (!NotificationTracker.hasFired("srv_${n.id}")) {
                showLocalNotification(n.title, n.body)
                NotificationTracker.markFired("srv_${n.id}")
                ApiService.acknowledgeNotification(n.id)
            }
        }
    }

    private fun checkEquipmentDueDates(equipment: List<Equipment>) {
        for (eq in equipment) {
            val days = eq.days_until_due ?: continue
            val baseId = "eq_${eq.equipment_id}"

            when {
                days < 0 && !NotificationTracker.hasFired("${baseId}_overdue") -> {
                    showLocalNotification("Service Overdue", "${eq.type_name} is overdue for service!")
                    NotificationTracker.markFired("${baseId}_overdue")
                }
                days <= 1 && days >= 0 && !NotificationTracker.hasFired("${baseId}_1") -> {
                    showLocalNotification("Service Due Tomorrow", "${eq.type_name} is due for service tomorrow")
                    NotificationTracker.markFired("${baseId}_1")
                }
                days <= 14 && days > 1 && !NotificationTracker.hasFired("${baseId}_14") -> {
                    showLocalNotification("Service Due Soon", "${eq.type_name} is due for service in $days days")
                    NotificationTracker.markFired("${baseId}_14")
                }
                days <= 30 && days > 14 && !NotificationTracker.hasFired("${baseId}_30") -> {
                    showLocalNotification("Service Due Soon", "${eq.type_name} is due for service in $days days")
                    NotificationTracker.markFired("${baseId}_30")
                }
            }
        }
    }

    private fun checkAppointments(appointments: List<Appointment>) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        for (apt in appointments) {
            val baseId = "apt_${apt.appointment_id}"
            val prev = NotificationTracker.getPreviousStatus(baseId)
            val current = apt.status

            if (prev != current) {
                when (current) {
                    "confirmed" -> {
                        if (prev == null || prev == "pending") {
                            showLocalNotification(
                                "Appointment Scheduled",
                                "Your ${apt.service_type} has been scheduled for ${apt.displayDate}"
                            )
                        }
                    }
                    "in_progress" -> {
                        showLocalNotification(
                            "Technician En Route",
                            "Your technician is on the way for ${apt.service_type}"
                        )
                    }
                    "completed" -> {
                        showLocalNotification(
                            "Service Complete",
                            "Your ${apt.service_type} appointment has been completed"
                        )
                    }
                }
                NotificationTracker.setPreviousStatus(baseId, current)
            }

            val dateStr = apt.confirmed_date ?: apt.requested_date ?: continue
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                val y = parts[0].toIntOrNull() ?: continue
                val m = parts[1].toIntOrNull() ?: continue
                val d = parts[2].toIntOrNull() ?: continue
                val aptDate = LocalDate(y, m, d)
                val tomorrow = today.plus(1, DateTimeUnit.DAY)
                if (aptDate == tomorrow && current in setOf("confirmed", "pending")) {
                    val dayId = "${baseId}_daybefore"
                    if (!NotificationTracker.hasFired(dayId)) {
                        showLocalNotification(
                            "Appointment Tomorrow",
                            "Your ${apt.service_type} is scheduled for tomorrow at ${apt.displayTime}"
                        )
                        NotificationTracker.markFired(dayId)
                    }
                }
            }
        }
    }

    private suspend fun checkInvoices() {
        val invoices = ApiService.getInvoices().getOrNull() ?: return
        for (inv in invoices) {
            val baseId = "inv_${inv.invoice_id}"
            val prev = NotificationTracker.getPreviousStatus(baseId)
            val current = inv.status

            if (prev != current) {
                when (current) {
                    "paid" -> {
                        showLocalNotification(
                            "Receipt Issued",
                            "Payment received for invoice #${inv.invoice_number}"
                        )
                    }
                    "sent" -> {
                        if (prev == null) {
                            showLocalNotification(
                                "Invoice Issued",
                                "Invoice #${inv.invoice_number} for $${inv.total} is ready"
                            )
                        }
                    }
                }
                NotificationTracker.setPreviousStatus(baseId, current)
            }
        }
    }
}
