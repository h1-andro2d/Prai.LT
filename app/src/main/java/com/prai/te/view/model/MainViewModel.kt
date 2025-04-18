package com.prai.te.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prai.te.auth.MainAuthManager
import com.prai.te.media.MainPlayer
import com.prai.te.media.MainVolumeReader
import com.prai.te.model.MainCallState
import com.prai.te.model.MainEvent
import com.prai.te.model.MainIntroState
import com.prai.te.model.MainTranslationState
import com.prai.te.retrofit.MainConversationMeta
import com.prai.te.retrofit.MainConversationResponse
import com.prai.te.retrofit.MainRetrofit
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {
    private val conversationFlowMap =
        mutableMapOf<String, MutableStateFlow<MainConversationResponse?>>()
    private val mutableEvent by lazy { MutableSharedFlow<MainEvent>() }

    val event by lazy { mutableEvent.asSharedFlow() }

    val isRecording = MutableStateFlow(false)
    val segmentItemList = MutableStateFlow<List<CallSegmentItem>>(emptyList())
    val callState = MutableStateFlow<MainCallState>(MainCallState.None)
    val isSettingOverlayVisible = MutableStateFlow(false)
    val isTranslationOverlayVisible = MutableStateFlow(false)
    val isMainSettingVisible = MutableStateFlow(false)
    val isProfileSettingVisible = MutableStateFlow(false)
    val isCallEndingDialog = MutableStateFlow(false)
    val isRecordingPermissionDialog = MutableStateFlow(false)
    val isServiceEndingDialog = MutableStateFlow(false)
    val translationState = MutableStateFlow<MainTranslationState>(MainTranslationState.None)
    val isAiSettingVisible = MutableStateFlow(false)
    val callTime = MutableStateFlow(0)
    val recordTime = MutableStateFlow(0)
    val currentSegment = MutableStateFlow<CallSegmentItem?>(null)
    val notification = MutableStateFlow<String?>(null)
    val volumeLevel = MutableStateFlow(0.1f)
    val isConversationListVisible = MutableStateFlow(false)
    val chatList = MutableStateFlow<List<MainConversationMeta>>(emptyList())
    val callResponseWaiting = MutableStateFlow(false)
    val selectedConversationId = MutableStateFlow<String?>(null)

    val introState = MutableStateFlow(MainIntroState.SPLASH)
    val introEndDialog = MutableStateFlow(false)
    val forceUpdateDialog = MutableStateFlow(false)
    val isLoginProcessing = MutableStateFlow(false)
    val isRegisterProcessing = MutableStateFlow(false)
    val isServerErrorDialog = MutableStateFlow(false)

    val isLogoutDialog = MutableStateFlow(false)
    val isDeleteUserDialog = MutableStateFlow(false)

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

            is MainRetrofit.Event.MinVersionResponse -> {
            }

            is MainRetrofit.Event.UserInfoNotFound -> {
                introState.value = MainIntroState.ONBOARDING
            }

            is MainRetrofit.Event.CallResponseError -> {
                callResponseWaiting.value = false
            }

            is MainRetrofit.Event.TranslationResponse -> {
                val state = translationState.value
                if (state is MainTranslationState.Requested &&
                    state.originalText == event.originalText
                ) {
                    translationState.value = MainTranslationState.Done(
                        state.originalText,
                        event.response.translation
                    )
                }
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

            else -> {

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
        currentSegment.value = items.getOrNull(0)
        segmentItemList.value = items
    }

    fun onCallStart() {
        initializeData()
        callState.value = MainCallState.Connecting
        dispatchEvent(MainEvent.CallStart)
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
        dispatchEvent(MainEvent.CallEnd)
    }

    fun onServiceEnd() {
        stopCallTimer()
        callState.value = MainCallState.None
        initializeData()
        isServiceEndingDialog.value = false
        dispatchEvent(MainEvent.ServiceEnd)
    }

    fun startRecording() {
        isRecording.value = true
        startRecordTimer()
    }

    fun sendRecordingRequest() {
        dispatchEvent(MainEvent.RecordStart)
    }

    fun stopRecording() {
        isRecording.value = false
        stopRecordTimer()
        dispatchEvent(MainEvent.RecordStop)
    }

    fun cancelRecording() {
        isRecording.value = false
        stopRecordTimer()
        dispatchEvent(MainEvent.RecordCancel)
    }

    fun openChatList() {
        isConversationListVisible.value = true
        dispatchEvent(MainEvent.ConversationListOpen)
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

    fun onTranslationStart() {
        if (segmentItemList.value.isEmpty()) {
            return
        }
        val textList = segmentItemList.value.map { it.text }
        val text = textList.joinToString(separator = " ").trim()
        if (text.isEmpty()) {
            return
        }
        val state = translationState.value
        isTranslationOverlayVisible.value = true
        if (state is MainTranslationState.Done && state.originalText == text) {
            return
        }
        if (state is MainTranslationState.Requested && state.originalText == text) {
            return
        }
        translationState.value = MainTranslationState.Requested(text)
        dispatchEvent(MainEvent.TranslationRequest(text))
    }

    fun launchDelayed(delay: Long, callback: () -> Unit) {
        viewModelScope.launch {
            delay(delay)
            callback.invoke()
        }
    }

    fun dispatchEvent(target: MainEvent) {
        viewModelScope.launch { mutableEvent.emit(target) }
    }

    fun onAuthEvent(event: MainAuthManager.Event) {
        when (event) {
            is MainAuthManager.Event.Connect -> {

            }

            else -> {

            }
        }
    }

    fun initializeData() {
        currentSegment.value = null
        isRecording.value = false
        isAiSettingVisible.value = false
        isCallEndingDialog.value = false
        chatList.value = emptyList()
        isSettingOverlayVisible.value = false
        isTranslationOverlayVisible.value = false
        isConversationListVisible.value = false
        isProfileSettingVisible.value = false
        isMainSettingVisible.value = false
        translationState.value = MainTranslationState.None
        volumeLevel.value = 0.1f
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