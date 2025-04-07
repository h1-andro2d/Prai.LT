package com.prai.te

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.prai.te.common.MainCodec
import com.prai.te.media.MainFileManager
import com.prai.te.media.MainPlayer
import com.prai.te.media.MainRecorder
import com.prai.te.media.MainVolumeReader
import com.prai.te.model.MainCallState
import com.prai.te.model.MainEvent
import com.prai.te.permission.MainPermission
import com.prai.te.permission.MainPermissionHandler
import com.prai.te.retrofit.MainCallSegment
import com.prai.te.retrofit.MainRetrofit
import com.prai.te.view.RootView
import com.prai.te.view.model.CallSegmentItem
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val viewModel: MainViewModel by viewModels()
    private val repository: MainRepositoryViewModel by viewModels()
    private val recorder: MainRecorder by lazy { MainRecorder(scope) }
    private val retrofit: MainRetrofit by lazy { MainRetrofit(scope.coroutineContext) }
    private val player: MainPlayer by lazy { MainPlayer(scope) }
    private val volumeReader: MainVolumeReader by lazy { MainVolumeReader(scope) }

    private val transparent = Color.Transparent.toArgb()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        setContent { RootView() }
    }

    override fun onResume() {
        super.onResume()
        retrofit.getConversationList()
    }

    override fun onDestroy() {
        scope.cancel()
        recorder.stop()
        volumeReader.stop()
        player.stop()
        MainFileManager.deleteAllAudioFiles(this)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        when (requestCode) {
            MainPermission.AUDIO.code -> handleRecordStartEvent()
            else -> Unit
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initialize() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(transparent),
            navigationBarStyle = SystemBarStyle.dark(transparent)
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        MainPermissionHandler.requestPermissions(this, MainPermission.AUDIO)
        collectMainEvent()
        collectRecorderEvent()
        collectRetrofitEvent()
        collectPlayerEvent()
        collectVolumeReaderEvent()
    }

    private fun collectMainEvent() {
        scope.launch {
            viewModel.event.collect {
                when (it) {
                    is MainEvent.RecordStart -> handleRecordStartEvent()
                    is MainEvent.RecordStop -> {
                        recorder.stop()
                        volumeReader.stop()
                    }

                    is MainEvent.RecordCancel -> {
                        recorder.cancel()
                        volumeReader.stop()
                    }

                    is MainEvent.ServiceEnd -> finish()

                    is MainEvent.PlayStart -> player.start(it.path)
                    is MainEvent.GoogleLoginRequest -> {}
                    is MainEvent.LogoutRequest -> {}
                    is MainEvent.NoCredential -> startAddAccountActivity()
                    is MainEvent.CallStart -> {
                        retrofit.sendFirstCallRequest(
                            repository.selectedVoiceSettingItem.value.code,
                            repository.selectedVibeSettingItem.value.code,
                            repository.selectedVoiceSpeed.value
                        )
                    }

                    is MainEvent.CallEnd -> player.stop()
                    is MainEvent.ConversationListOpen -> retrofit.getConversationList()
                    is MainEvent.ConversationOpen -> retrofit.getConversation(it.id)
                    is MainEvent.TranslationRequest -> retrofit.getTranslation(it.text)
                }
            }
        }
    }

    private fun startAddAccountActivity() {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        startActivity(intent)
    }

    private fun collectRecorderEvent() {
        scope.launch {
            recorder.event.collect { event ->
                when (event) {
                    is MainRecorder.Event.Success -> {
                        handleRecordSuccessEvent(event.path)
                    }
                }
            }
        }
    }

    private fun handleRecordSuccessEvent(path: String) {
        val state = viewModel.callState.value
        if (state is MainCallState.Active) {
            retrofit.sendCallRequest(
                path,
                repository.selectedVoiceSettingItem.value.code,
                repository.selectedVibeSettingItem.value.code,
                repository.selectedVoiceSpeed.value,
                state.id
            )
            viewModel.callResponseWaiting.value = true
            player.stop()
        }
    }

    private fun handleRecordStartEvent() {
        if (MainPermissionHandler.isGranted(this, MainPermission.AUDIO)) {
            recorder.start(this)
            volumeReader.start(this)
        } else {
            MainPermissionHandler.requestPermissions(this, MainPermission.AUDIO)
        }
    }

    private fun collectRetrofitEvent() {
        scope.launch {
            retrofit.event.collect { event ->
                when (event) {
                    is MainRetrofit.Event.FirstCallResponse -> {
                        handleCallResponse(event.response.segments)
                    }

                    is MainRetrofit.Event.CallResponse -> {
                        handleCallResponse(event.response.segments)
                    }

                    else -> Unit
                }
                viewModel.onRetrofitEvent(event)
            }
        }
    }

    private fun handleCallResponse(segments: List<MainCallSegment>) {
        val items = segments.mapNotNull { segment ->
            val path = MainFileManager.createAudioFilePath(this)
            if (MainCodec.decodeBase64ToFile(segment.audio, path)) {
                CallSegmentItem(segment.text, path)
            } else {
                null
            }
        }
        viewModel.updateSegmentItemList(items)
        viewModel.callResponseWaiting.value = false
        player.start(items)
    }

    private fun collectPlayerEvent() {
        scope.launch { player.event.collect { viewModel.onPlayerEvent(it) } }
    }

    private fun collectVolumeReaderEvent() {
        scope.launch { volumeReader.event.collect { viewModel.onVolumeEvent(it) } }
    }
}