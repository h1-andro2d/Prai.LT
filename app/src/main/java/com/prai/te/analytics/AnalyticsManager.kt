package com.prai.te.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.prai.te.common.MainLogger

// TODO remove this class
internal object AnalyticsManager {
    private var manager: FirebaseAnalytics? = null

    fun initialize(context: Context) {
        manager = FirebaseAnalytics.getInstance(context)
    }

    fun logEvent() {
        val params = Bundle().apply {
            putString("name", "talker")
            putString("action", "click")
        }
        manager?.setUserId("testUser")
        manager?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        manager?.logEvent("custom_click", params)
        MainLogger.Analytics.log("logEvent, $params")
    }
}