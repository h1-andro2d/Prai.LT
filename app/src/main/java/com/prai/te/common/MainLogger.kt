package com.prai.te.common

import android.util.Log

internal sealed class MainLogger {
    protected abstract val tag: String

    fun log(message: String) {
        Log.d(MAIN_TAG + DELIMITER + tag, message)
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

    companion object {
        private const val MAIN_TAG = "MainLogger"
        private const val DELIMITER = "."
    }
}