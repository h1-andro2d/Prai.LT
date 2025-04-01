package com.prai.te.permission

internal enum class MainPermission(val items: List<String>, val code: Int) {
    AUDIO(listOf(android.Manifest.permission.RECORD_AUDIO), 1001)
}