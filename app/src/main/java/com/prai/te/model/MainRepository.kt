package com.prai.te.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

internal class MainRepository(context: Context = Application()) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var selectedVoiceSpeed: Float
        get() = sharedPreferences.getFloat("speed", 0.5f)
        set(value) = sharedPreferences.edit { putFloat("speed", value) }

    var selectedVoiceSettingItem: MainVoiceSettingItem
        get() {
            val value = sharedPreferences.getString("voice_setting", MainVoiceSettingItem.FEMALE_1.name)
            return MainVoiceSettingItem.valueOf(value ?: MainVoiceSettingItem.FEMALE_1.name)
        }
        set(value) = sharedPreferences.edit { putString("voice_setting", value.name) }

    var selectedVibeSettingItem: MainVibeSettingItem
        get() {
            val value = sharedPreferences.getString("vibe_setting", MainVibeSettingItem.FRIENDLY.name)
            return MainVibeSettingItem.valueOf(value ?: MainVibeSettingItem.FRIENDLY.name)
        }
        set(value) = sharedPreferences.edit { putString("vibe_setting", value.name) }

    var selectedBirthDateMills: Long
        get() = sharedPreferences.getLong("birth_date", 1577836800000L)
        set(value) = sharedPreferences.edit { putLong("birth_date", value) }

    var freeTrialTime: Long
        get() = sharedPreferences.getLong("freeTrial", 0)
        set(value) = sharedPreferences.edit { putLong("freeTrial", value) }
}