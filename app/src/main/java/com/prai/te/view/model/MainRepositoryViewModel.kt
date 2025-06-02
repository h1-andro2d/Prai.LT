package com.prai.te.view.model

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.prai.te.model.MainRepository
import com.prai.te.retrofit.MainUserInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class MainRepositoryViewModel(
    application: Application = Application()
) : AndroidViewModel(application) {
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
    var ageCache = ageText.value
    var genderCache = selectedGender.value
    var nameCache = nameText.value

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

        Firebase.analytics.logEvent(
            "voice_settings_saved",
            bundleOf(
                "voice_type" to selectedVoiceSettingItem.value.code,
                "tone" to selectedVibeSettingItem.value.code,
                "speed" to selectedVoiceSpeed.value,
            )
        )
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

    fun saveFreeTrialTime() {
        repository.freeTrialTime = System.currentTimeMillis()
    }

    fun canStartFreeTrial(): Boolean {
        if (repository.freeTrialTime == 0L) {
            return true
        }
        val day1 = 1000 * 60 * 60 * 24
        val isValid = System.currentTimeMillis() - repository.freeTrialTime >= day1
        return isValid
    }

    sealed interface Event {
        data class SaveUserInfo(val name: String, val age: String, val gender: UserGender) : Event
    }
}