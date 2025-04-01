package com.prai.te.view.call

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.common.FadeView
import com.prai.te.model.MainCallState
import com.prai.te.view.model.MainViewModel

@Composable
internal fun CallView(model: MainViewModel = viewModel()) {
    val state = model.callState.collectAsStateWithLifecycle()
    val isRecording = model.isRecording.collectAsStateWithLifecycle()
    val isSettingOverlayVisible = model.isSettingOverlayVisible.collectAsStateWithLifecycle()
    val currentSegment = model.currentSegment.collectAsStateWithLifecycle()
    val isAiSettingVisible = model.isAiSettingVisible.collectAsStateWithLifecycle()
    val isChatListVisible = model.isConversationListVisible.collectAsStateWithLifecycle()
    val notifiation = model.isAiSettingVisible.collectAsStateWithLifecycle()
    val blurEffect = remember {
        android.graphics.RenderEffect
            .createBlurEffect(20f, 20f, android.graphics.Shader.TileMode.DECAL)
            .asComposeRenderEffect()
    }

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
            .graphicsLayer {
                renderEffect = if (isSettingOverlayVisible.value) blurEffect else null
            }
            .background(color = Color(0xFF000000))
    ) {
        Text(
            text = "PRAI",
            fontSize = 36.sp,
            color = Color(0xFFFFFFFF),
            modifier = Modifier
                .padding(top = 63.dp)
                .align(Alignment.TopCenter)
        )
        FadeView(
            visible = state.value is MainCallState.Connecting,
            modifier = Modifier
                .padding(top = 113.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "연결중",
                fontSize = 18.sp,
                color = Color(0xFF959595),
                fontWeight = FontWeight.W500
            )
        }
        FadeView(
            visible = state.value is MainCallState.Active,
            modifier = Modifier
                .padding(top = 113.dp)
                .align(Alignment.TopCenter)
        ) {
            TimeText()
        }
        FadeView(
            visible = state.value is MainCallState.Active && currentSegment.value != null,
            modifier = Modifier
                .padding(top = 408.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = currentSegment.value?.text ?: "",
                fontSize = 16.sp,
                lineHeight = 22.4.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

        }
        TalkerBox(
            state = state.value,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 79.dp)
        )
        FadeView(
            visible = state.value == MainCallState.None,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 167.dp)
        ) {
            GuideText()
        }
        FadeView(
            visible = isRecording.value, modifier = Modifier
                .padding(bottom = 163.dp)
                .align(Alignment.BottomCenter)
        ) {
            RecordingView()
        }
        NotificationText(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 176.dp)
        )
        MenuRowView(
            isRecording = isRecording.value,
            state = state.value,
            modifier = Modifier
                .padding(bottom = 45.dp)
                .align(Alignment.BottomCenter)
        )
    }
    FadeView(
        visible = isSettingOverlayVisible.value,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        MenuOverlayView()
    }
    AnimatedVisibility(
        visible = isAiSettingVisible.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier
            .fillMaxSize()
    ) {
        AiSettingView()
    }
    FadeView(
        visible = isChatListVisible.value,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        ConversationListView()
    }
}

@Composable
private fun TimeText(model: MainViewModel = viewModel()) {
    val seconds = model.callTime.collectAsStateWithLifecycle()
    val minutes = seconds.value / 60
    val remainingSeconds = seconds.value % 60
    Text(
        text = String.format(
            LocalConfiguration.current.locales.get(0),
            "%02d:%02d",
            minutes,
            remainingSeconds
        ),
        fontSize = 18.sp,
        color = Color(0xFFFFFFFF),
        fontWeight = FontWeight.W500
    )
}

@Composable
private fun NotificationText(modifier: Modifier, model: MainViewModel = viewModel()) {
    val notification = model.notification.collectAsStateWithLifecycle()

    FadeView(
        visible = notification.value != null,
        modifier = modifier
    ) {
        Text(
            text = notification.value ?: "",
            fontSize = 14.sp,
            lineHeight = 19.6.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .background(
                    color = Color(0x66515151),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 18.dp, vertical = 13.dp)
        )
    }
}

@Composable
private fun GuideText() {
    Text(
        text = "언제든 편하게 연락하세요!",
        fontSize = 16.sp,
        color = Color(0xFFFFFFFF)
    )
}

@Preview
@Composable
private fun CallPreview() {
    CallView()
}