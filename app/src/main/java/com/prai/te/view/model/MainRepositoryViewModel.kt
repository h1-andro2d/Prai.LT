package com.prai.te.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prai.te.model.MainRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


internal class MainRepositoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainRepository(application)

    val selectedVoiceSpeed = MutableStateFlow(repository.selectedVoiceSpeed)
    val selectedVoiceSettingItem = MutableStateFlow(repository.selectedVoiceSettingItem)
    val selectedVibeSettingItem = MutableStateFlow(repository.selectedVibeSettingItem)

    val selectedBirthDateMills = MutableStateFlow<Long?>(repository.selectedBirthDateMills)
    val selectedGender = MutableStateFlow(repository.selectedGender)
    val nameText = MutableStateFlow(repository.nameText)

    var voiceSpeedCache = selectedVoiceSpeed.value
    var voiceSettingCache = selectedVoiceSettingItem.value
    var vibeSettingCache = selectedVibeSettingItem.value

    var birthDateCache = selectedBirthDateMills.value
    var genderCache = selectedGender.value
    var nameCache = nameText.value

    fun makeAiSettingCache() {
        voiceSpeedCache = selectedVoiceSpeed.value
        voiceSettingCache = selectedVoiceSettingItem.value
        vibeSettingCache = selectedVibeSettingItem.value
    }

    fun saveCurrentAiSetting() {
        repository.selectedVoiceSpeed = selectedVoiceSpeed.value
        repository.selectedVoiceSettingItem = selectedVoiceSettingItem.value
        repository.selectedVibeSettingItem = selectedVibeSettingItem.value
    }

    fun rollbackAiSetting() {
        viewModelScope.launch {
            delay(300L)
            selectedVoiceSpeed.value = voiceSpeedCache
            selectedVoiceSettingItem.value = voiceSettingCache
            selectedVibeSettingItem.value = vibeSettingCache
        }
    }

    fun makeProfileSettingCache() {
        birthDateCache = selectedBirthDateMills.value
        genderCache = selectedGender.value
        nameCache = nameText.value
    }

    fun saveProfileSetting() {
        repository.selectedBirthDateMills = selectedBirthDateMills.value ?:1577836800000L
        repository.selectedGender = selectedGender.value
        repository.nameText = nameText.value
    }

    fun rollbackProfileSetting() {
        viewModelScope.launch {
            selectedBirthDateMills.value = birthDateCache
            selectedGender.value = genderCache
            nameText.value = nameCache
        }
    }
}