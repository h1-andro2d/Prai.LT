package com.prai.te.common

import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.crashlytics

internal sealed class MainLogger {
    protected abstract val tag: String

    fun log(message: String) {
        Log.d(MAIN_TAG + DELIMITER + tag, message)
    }

    fun log(exception: Exception, message: String) {
        val customKeysAndValues = CustomKeysAndValues.Builder()
            .putString("message", message)
            .build()
        Log.d(MAIN_TAG + DELIMITER + tag, message)
        Firebase.crashlytics.recordException(exception, customKeysAndValues)
        Firebase.analytics.logEvent("custom_exception", bundleOf("message" to message))
    }

    data object Recorder : MainLogger() {
        override val tag = "Recorder"
    }

    data object Player : MainLogger() {
        override val tag = "Player"
    }

    data object Retrofit : MainLogger() {
        override val tag = "Retrofit"
    }

    data object Codec : MainLogger() {
        override val tag = "Codec"
    }

    data object Auth : MainLogger() {
        override val tag = "Auth"
    }

    data object VolumeReader : MainLogger() {
        override val tag = "VolumeReader"
    }

    data object Activity : MainLogger() {
        override val tag = "Activity"
    }

    data class View(private val name: String) : MainLogger() {
        override val tag = name
    }

    data object Billing : MainLogger() {
        override val tag = "Billing"
    }

    data object Security : MainLogger() {
        override val tag = "Security"
    }

    data object Analytics : MainLogger() {
        override val tag = "Analytics"
    }

    data object Navigator : MainLogger() {
        override val tag = "Navigator"
    }

    data object ETC : MainLogger() {
        override val tag = "ETC"
    }

    companion object {
        private const val MAIN_TAG = "MainLogger"
        private const val DELIMITER = "."
    }
}