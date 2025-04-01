package com.prai.te.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prai.te.media.MainPlayer
import com.prai.te.media.MainVolumeReader
import com.prai.te.model.MainCallState
import com.prai.te.model.MainEvent
import com.prai.te.model.MainVoiceSettingItem
import com.prai.te.model.MainVibeSettingItem
import com.prai.te.retrofit.MainConversationMeta
import com.prai.te.retrofit.MainConversationResponse
import com.prai.te.retrofit.MainRetrofit
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {
    val event = MutableSharedFlow<MainEvent>()
    val isRecording = MutableStateFlow(false)
    val speed = MutableStateFlow(0.5f)
    val segmentItemList = MutableStateFlow<List<CallSegmentItem>>(emptyList())
    val callState = MutableStateFlow<MainCallState>(MainCallState.None)
    val isSettingOverlayVisible = MutableStateFlow(false)
    val isAiSettingVisible = MutableStateFlow(false)
    val callTime = MutableStateFlow(0)
    val recordTime = MutableStateFlow(0)
    val currentSegment = MutableStateFlow<CallSegmentItem?>(null)
    val notification = MutableStateFlow<String?>(null)
    val volumeLevel = MutableStateFlow(0.1f)
    val isConversationListVisible = MutableStateFlow(false)
    val chatList = MutableStateFlow<List<MainConversationMeta>>(emptyList())

    val selectedVoiceSettingItem = MutableStateFlow(MainVoiceSettingItem.FEMALE_1)
    val selectedVibeSettingItem = MutableStateFlow(MainVibeSettingItem.FRIENDLY)
    val selectedConversationId = MutableStateFlow<String?>(null)

    private val conversationFlowMap =
        mutableMapOf<String, MutableStateFlow<MainConversationResponse?>>()

    private var callTimer: Timer? = null
        set(value) {
            if (field != value) {
                field?.cancel()
                field = value
            }
        }
    private var recordTimer: Timer? = null
        set(value) {
            if (field != value) {
                field?.cancel()
                field = value
            }
        }
    private var notificationJob: Job? = null
        set(value) {
            if (field != value) {
                field?.cancel()
                field = value
            }
        }

    fun onRetrofitEvent(event: MainRetrofit.Event) {
        when (event) {
            is MainRetrofit.Event.CallResponse -> {

            }

            is MainRetrofit.Event.ConversationResponse -> {
                val id = event.response.conversationId
                val flow = conversationFlowMap.getOrPut(id) {
                    MutableStateFlow(null)
                }
                flow.value = event.response
            }

            is MainRetrofit.Event.ConversationListResponse -> {
                chatList.value = event.response.conversations
            }

            is MainRetrofit.Event.FirstCallResponse -> {
                if (callState.value is MainCallState.Connecting) {
                    onCallConnected(event.response.conversationId)
                }
            }
        }
    }

    fun onPlayerEvent(event: MainPlayer.Event) {
        when (event) {
            is MainPlayer.Event.Playing -> currentSegment.value = event.segment
            is MainPlayer.Event.End -> currentSegment.value = null
        }
    }

    fun onVolumeEvent(event: MainVolumeReader.Event) {
        when (event) {
            is MainVolumeReader.Event.Success -> volumeLevel.value = event.level
        }
    }

    fun getConversationFlow(id: String): StateFlow<MainConversationResponse?> {
        return conversationFlowMap.getOrPut(id) { MutableStateFlow(null) }.asStateFlow()
    }

    fun updateSegmentItemList(items: List<CallSegmentItem>) {
        segmentItemList.value = items
    }

    fun onCallStart() {
        initializeData()
        callState.value = MainCallState.Connecting
        viewModelScope.launch { event.emit(MainEvent.CallStart) }
    }

    fun onCallConnected(id: String) {
        callTime.value = 0
        startCallTimer()
        callState.value = MainCallState.Active(id)
    }

    fun onCallEnd() {
        stopCallTimer()
        callState.value = MainCallState.None
        initializeData()
        viewModelScope.launch { event.emit(MainEvent.CallEnd) }
    }

    fun startRecording() {
        isRecording.value = true
        startRecordTimer()
        viewModelScope.launch { event.emit(MainEvent.RecordStart) }
    }

    fun stopRecording() {
        isRecording.value = false
        stopRecordTimer()
        viewModelScope.launch { event.emit(MainEvent.RecordStop) }
    }

    fun cancelRecording() {
        isRecording.value = false
        stopRecordTimer()
        viewModelScope.launch { event.emit(MainEvent.RecordCancel) }
    }

    fun openChatList() {
        isConversationListVisible.value = true
        viewModelScope.launch { event.emit(MainEvent.ConversationListOpen) }
    }

    fun closeChatList() {
        isConversationListVisible.value = false
    }

    fun showNotification(text: String) {
        notificationJob = viewModelScope.launch {
            notification.value = text
            delay(3000L)
            notification.value = null
        }
    }

    private fun initializeData() {
        currentSegment.value = null
        isRecording.value = false
        isAiSettingVisible.value = false
        isSettingOverlayVisible.value = false
        segmentItemList.value = emptyList()
    }

    private fun startCallTimer() {
        callTime.value = 0
        callTimer = fixedRateTimer("call_timer", true, 0L, 1000) { callTime.value += 1 }
    }

    private fun stopCallTimer() {
        callTimer = null
    }

    private fun startRecordTimer() {
        recordTime.value = 0
        recordTimer = fixedRateTimer("record_timer", true, 0L, 1000) {
            recordTime.value += 1
            if (recordTime.value == 60) {
                showNotification("1분이 초과되어 자동으로 전송되었습니다.")
                stopRecording()
            }
        }
    }

    private fun stopRecordTimer() {
        recordTimer = null
    }
}