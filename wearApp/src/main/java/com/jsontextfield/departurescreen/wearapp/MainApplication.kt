package com.jsontextfield.departurescreen.wearapp

import android.app.Application
import com.jsontextfield.departurescreen.wearapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
        }
    }
}