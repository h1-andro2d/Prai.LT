package com.prai.te.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prai.te.model.MainRepository
import com.prai.te.retrofit.MainUserInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class MainRepositoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainRepository(application)

    val event = MutableSharedFlow<Event>()

    val selectedVoiceSpeed = MutableStateFlow(repository.selectedVoiceSpeed)
    val selectedVoiceSettingItem = MutableStateFlow(repository.selectedVoiceSettingItem)
    val selectedVibeSettingItem = MutableStateFlow(repository.selectedVibeSettingItem)

    val selectedBirthDateMills = MutableStateFlow<Long?>(repository.selectedBirthDateMills)
    val ageText = MutableStateFlow("")
    val selectedGender = MutableStateFlow<UserGender?>(null)
    val nameText = MutableStateFlow("")

    var userId: String? = null
    var email: String? = null

    private var voiceSpeedCache = selectedVoiceSpeed.value
    private var voiceSettingCache = selectedVoiceSettingItem.value
    private var vibeSettingCache = selectedVibeSettingItem.value

    private var birthDateCache = selectedBirthDateMills.value
    private var ageCache = ageText.value
    private var genderCache = selectedGender.value
    private var nameCache = nameText.value

    fun reset() {
        ageText.value = ""
        selectedGender.value = null
        nameText.value = ""
        email = null
        userId = null
    }

    fun updateServerUserInfo(info: MainUserInfo) {
        ageText.value = info.birthYear?.takeLast(2) ?: ""
        nameText.value = info.name
        selectedGender.value = UserGender.entries.find { it.code == info.gender }
        email = info.email
        userId = info.userId
    }

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
        ageCache = ageText.value
        genderCache = selectedGender.value
        nameCache = nameText.value
    }

    fun saveProfileSetting() {
        val gender = selectedGender.value ?: return
        viewModelScope.launch {
            event.emit(
                Event.SaveUserInfo(
                    name = nameText.value,
                    age = ageText.value,
                    gender = gender
                )
            )
        }
    }

    fun rollbackProfileSetting() {
        viewModelScope.launch {
            selectedBirthDateMills.value = birthDateCache
            ageText.value = ageCache
            selectedGender.value = genderCache
            nameText.value = nameCache
        }
    }

    sealed interface Event {
        data class SaveUserInfo(val name: String, val age: String, val gender: UserGender) : Event
    }
}