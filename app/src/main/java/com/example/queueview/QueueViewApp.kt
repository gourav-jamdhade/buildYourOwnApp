package com.example.queueview

import android.app.Application
import android.util.Log
import com.example.queueview.di.appModule
import com.google.firebase.Firebase
import com.google.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QueueViewApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Firebase FIRST
        Firebase.initialize(this)

        // Start work manager when app launches

        // 2. Start Koin with timeout
        try {
            startKoin {
                androidContext(this@QueueViewApp)
                modules(appModule)
            }.also {
                Log.d("KOIN", "Initialization successful")
            }
        } catch (e: Exception) {
            Log.e("KOIN", "Init failed", e)
            throw e
        }

    }
}