package com.crotsertech.waterairandyoumvp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.service.PollingService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidApp.init(application)
        enableEdgeToEdge()
        setContent {
            App()

            val lifecycleOwner = LocalLifecycleOwner.current
            LaunchedEffect(lifecycleOwner) {
                lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: androidx.lifecycle.LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_START) {
                            if (ApiService.isLoggedIn.value) {
                                startPollingService()
                            }
                        }
                    }
                })
            }

            val isLoggedIn by ApiService.isLoggedIn.collectAsState()
            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    startPollingService()
                } else {
                    stopPollingService()
                }
            }
        }
    }

    private fun startPollingService() {
        val intent = Intent(this, PollingService::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopPollingService() {
        val intent = Intent(this, PollingService::class.java).apply {
            action = PollingService.ACTION_STOP
        }
        startService(intent)
    }
}
