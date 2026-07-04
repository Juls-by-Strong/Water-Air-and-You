package com.crotsertech.waterairandyoumvp.data.api

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow

object TokenRepository {
    // Note: MapSettings is in-memory. For persistent storage, use Settings() factory
    // which delegates to SharedPreferences (Android) or NSUserDefaults (iOS).
    private val settings: Settings = Settings()

    private val _accessToken = MutableStateFlow(settings.getString("access_token", ""))
    private val _refreshToken = MutableStateFlow(settings.getString("refresh_token", ""))
    private val _customerId = MutableStateFlow(settings.getInt("customer_id", 0))

    var accessToken: String
        get() = _accessToken.value
        private set(value) {
            _accessToken.value = value
            settings.putString("access_token", value)
        }

    var refreshToken: String
        get() = _refreshToken.value
        private set(value) {
            _refreshToken.value = value
            settings.putString("refresh_token", value)
        }

    var customerId: Int
        get() = _customerId.value
        private set(value) {
            _customerId.value = value
            settings.putInt("customer_id", value)
        }

    fun saveTokens(access: String, refresh: String, customerId: Int) {
        this.accessToken = access
        this.refreshToken = refresh
        this.customerId = customerId
    }

    fun saveCredentials(email: String, password: String) {
        settings.putString("saved_email", email)
        settings.putString("saved_password", password)
    }

    fun clearCredentials() {
        settings.putString("saved_email", "")
        settings.putString("saved_password", "")
    }

    val savedEmail: String get() = settings.getString("saved_email", "")
    val savedPassword: String get() = settings.getString("saved_password", "")

    var hasCompletedFirstRun: Boolean
        get() = settings.getBoolean("first_run_complete", false)
        set(value) { settings.putBoolean("first_run_complete", value) }

    var useMetro: Boolean
        get() = settings.getBoolean("theme_metro", false)
        set(value) { settings.putBoolean("theme_metro", value) }

    var useDark: Boolean
        get() = settings.getBoolean("theme_dark", false)
        set(value) { settings.putBoolean("theme_dark", value) }

    var saltReminderIntervalDays: Int
        get() = settings.getInt("salt_interval", 30)
        set(value) { settings.putInt("salt_interval", value) }

    var saltLastResetEpochMs: Long
        get() = settings.getLong("salt_last_reset", 0L)
        set(value) { settings.putLong("salt_last_reset", value) }

    fun clearTokens() {
        accessToken = ""
        refreshToken = ""
        customerId = 0
        settings.putString("access_token", "")
        settings.putString("refresh_token", "")
        settings.putInt("customer_id", 0)
    }
}
