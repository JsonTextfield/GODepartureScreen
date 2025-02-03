package com.jsontextfield.departurescreen

import android.app.Application
import com.jsontextfield.departurescreen.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}