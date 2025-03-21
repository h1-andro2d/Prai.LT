package com.prai.lt.common

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

    companion object {
        private const val MAIN_TAG = "MainLogger"
        private const val DELIMITER = "."
    }
}