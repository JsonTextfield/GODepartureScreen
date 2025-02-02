package com.jsontextfield.departurescreen.android

import android.app.Application
import com.jsontextfield.departurescreen.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}