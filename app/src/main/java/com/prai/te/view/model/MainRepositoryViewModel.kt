package com.prai.te.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prai.te.model.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


internal class MainRepositoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainRepository(application)

    val selectedVoiceSpeed = MutableStateFlow(repository.selectedVoiceSpeed)
    val selectedVoiceSettingItem = MutableStateFlow(repository.selectedVoiceSettingItem)
    val selectedVibeSettingItem = MutableStateFlow(repository.selectedVibeSettingItem)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            selectedVoiceSpeed.collect { newValue ->
                repository.selectedVoiceSpeed = newValue
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            selectedVoiceSettingItem.collect { newValue ->
                repository.selectedVoiceSettingItem = newValue
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            selectedVibeSettingItem.collect { newValue ->
                repository.selectedVibeSettingItem = newValue
            }
        }
    }
}