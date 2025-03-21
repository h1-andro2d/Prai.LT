package com.prai.lt.view.model

import androidx.lifecycle.ViewModel
import com.prai.lt.model.MainEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

internal class MainViewModel : ViewModel() {
    val event = MutableSharedFlow<MainEvent>()
    val recordPathList = MutableStateFlow<List<String>>(emptyList())

    fun addRecordPath(string: String) {
        recordPathList.value += string
    }
}