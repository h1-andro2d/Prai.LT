package com.prai.lt

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.prai.lt.media.MainPlayer
import com.prai.lt.media.MainRecorder
import com.prai.lt.model.MainEvent
import com.prai.lt.permission.MainPermission
import com.prai.lt.permission.MainPermissionHandler
import com.prai.lt.view.RecordView
import com.prai.lt.view.model.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val transparent = Color.Transparent.toArgb()
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private val viewModel: MainViewModel by viewModels()
    private val recorder: MainRecorder by lazy { MainRecorder(scope) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        setContent { RootView() }
    }

    override fun onDestroy() {
        scope.cancel()
        recorder.stop()
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
    }

    private fun collectMainEvent() {
        scope.launch {
            viewModel.event.collect {
                when (it) {
                    is MainEvent.RecordStart -> handleRecordStartEvent()
                    is MainEvent.RecordStop -> recorder.stop()
                    is MainEvent.PlayStart -> MainPlayer.start(it.path)
                }
            }
        }
    }

    private fun collectRecorderEvent() {
        scope.launch {
            recorder.event.collect { event ->
                when (event) {
                    is MainRecorder.Event.Success -> viewModel.addRecordPath(event.path)
                }
            }
        }
    }

    private fun handleRecordStartEvent() {
        if (MainPermissionHandler.isGranted(this, MainPermission.AUDIO)) {
            recorder.start(this)
        } else {
            MainPermissionHandler.requestPermissions(this, MainPermission.AUDIO)
        }
    }
}

@Composable
private fun RootView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF271F47))
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        RecordView()
    }
}