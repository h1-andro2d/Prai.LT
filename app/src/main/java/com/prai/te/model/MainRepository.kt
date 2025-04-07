package com.prai.te.model

import android.content.Context
import android.content.SharedPreferences

internal class MainRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var selectedVoiceSpeed: Float
        get() = sharedPreferences.getFloat("speed", 0.5f)
        set(value) = sharedPreferences.edit().putFloat("speed", value).apply()

    var selectedVoiceSettingItem: MainVoiceSettingItem
        get() {
            val value = sharedPreferences.getString("voice_setting", MainVoiceSettingItem.FEMALE_1.name)
            return MainVoiceSettingItem.valueOf(value ?: MainVoiceSettingItem.FEMALE_1.name)
        }
        set(value) = sharedPreferences.edit().putString("voice_setting", value.name).apply()

    var selectedVibeSettingItem: MainVibeSettingItem
        get() {
            val value = sharedPreferences.getString("vibe_setting", MainVibeSettingItem.FRIENDLY.name)
            return MainVibeSettingItem.valueOf(value ?: MainVibeSettingItem.FRIENDLY.name)
        }
        set(value) = sharedPreferences.edit().putString("vibe_setting", value.name).apply()
}