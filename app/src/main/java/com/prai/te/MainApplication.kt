package com.prai.te

import android.app.Application
import com.prai.te.analytics.AnalyticsManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AnalyticsManager.initialize(this)
    }
}