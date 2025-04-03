package com.example.queueview

import android.app.Application
import com.example.queueview.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QueueViewApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@QueueViewApp)
            modules(appModule)
        }
    }
}