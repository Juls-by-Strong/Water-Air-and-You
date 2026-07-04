package com.crotsertech.waterairandyoumvp

import android.app.Application

object AndroidApp {
    lateinit var context: android.content.Context
        private set

    fun init(app: Application) {
        context = app.applicationContext
    }
}
