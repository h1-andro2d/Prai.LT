package com.prai.te.view.call

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.FadeView
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainCallState
import com.prai.te.view.AlphaAnimationText
import com.prai.te.view.LoadingDotAnimation2
import com.prai.te.view.common.TwoButtonDialog
import com.prai.te.view.model.MainViewModel

@Composable
internal fun CallView(model: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val state = model.callState.collectAsStateWithLifecycle()
    val isRecording = model.isRecording.collectAsStateWithLifecycle()
    val isRecordingPermissionDialog =
        model.isRecordingPermissionDialog.collectAsStateWithLifecycle()
    val isServiceDialogVisible = model.isServiceEndingDialog.collectAsStateWithLifecycle()
    val isSettingOverlayVisible = model.isSettingOverlayVisible.collectAsStateWithLifecycle()
    val isCallEndDialog = model.isCallEndingDialog.collectAsStateWithLifecycle()
    val isTranslationOverlayVisible =
        model.isTranslationOverlayVisible.collectAsStateWithLifecycle()
    val currentSegment = model.currentSegment.collectAsStateWithLifecycle()
    val isAiSettingVisible = model.isAiSettingVisible.collectAsStateWithLifecycle()
    val isChatListVisible = model.isConversationListVisible.collectAsStateWithLifecycle()
    val callResponseWaiting = model.callResponseWaiting.collectAsStateWithLifecycle()
    val blurEffect = remember {
        android.graphics.RenderEffect
            .createBlurEffect(20f, 20f, android.graphics.Shader.TileMode.DECAL)
            .asComposeRenderEffect()
    }
    BackHandler {
        if (isRecording.value) {
            model.cancelRecording()
        } else {
            if (state.value is MainCallState.None) {
                model.isServiceEndingDialog.value = true
            } else {
                model.isCallEndingDialog.value = true
            }
        }
    }

    val shouldBlur = isSettingOverlayVisible.value ||
            isTranslationOverlayVisible.value ||
            isCallEndDialog.value ||
            isRecordingPermissionDialog.value

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
            .graphicsLayer {
                renderEffect = if (shouldBlur) blurEffect else null
            }
            .background(color = Color(0xFF000000))
    ) {
        Text(
            text = "PRAI",
            fontFamily = MainFont.Pretendard,
            fontSize = 36.textDp,
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
                fontFamily = MainFont.Pretendard,
                fontSize = 18.textDp,
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
            visible = state.value is MainCallState.None,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(R.drawable.main_button_profile),
                contentDescription = null,
                modifier = Modifier
                    .cleanClickable { model.isMainSettingVisible.value = true }
                    .padding(top = 10.dp, end = 16.dp)
                    .size(33.dp)
            )
        }
        FadeView(
            visible = state.value is MainCallState.Active && currentSegment.value != null,
            modifier = Modifier
                .padding(top = 408.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = currentSegment.value?.text ?: "",
                fontFamily = MainFont.Pretendard,
                fontSize = 16.textDp,
                lineHeight = 22.textDp,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

        }
        TalkerBox(
            currentSegment.value,
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
        AnimatedVisibility(
            callResponseWaiting.value,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 260.dp),
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 0))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingDotAnimation2()
                AlphaAnimationText()
            }
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
        FadeView(
            visible = callResponseWaiting.value.not(),
            modifier = Modifier
                .padding(bottom = 45.dp)
                .align(Alignment.BottomCenter)
        ) {
            MenuRowView(
                currentSegment.value,
                isRecording = isRecording.value,
                state = state.value,
                modifier = Modifier
            )
        }
    }
    FadeView(
        visible = isSettingOverlayVisible.value,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        MenuOverlayView()
    }
    FadeView(
        visible = isCallEndDialog.value,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        TwoButtonDialog(
            titleText = "오늘 대화는 여기까지 할까요?",
            messageText = "오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.",
            endButtonText = "종료",
            cancelButtonText = "취소",
            onLeftButtonClick = {
                model.onCallEnd()
                model.isCallEndingDialog.value = false
            },
            onRightButtonClick = { model.isCallEndingDialog.value = false },
            onBackHandler = { model.isCallEndingDialog.value = false }
        )
    }
    FadeView(
        visible = isServiceDialogVisible.value,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        TwoButtonDialog(
            titleText = "통화를 종료할까요?",
            messageText = "언제든 다시 시작할 수 있어요!",
            endButtonText = "종료",
            cancelButtonText = "취소",
            onLeftButtonClick = {
                model.onServiceEnd()
                model.isServiceEndingDialog.value = false
            },
            onRightButtonClick = { model.isServiceEndingDialog.value = false },
            onBackHandler = { model.isServiceEndingDialog.value = false }
        )
    }
    FadeView(
        visible = isRecordingPermissionDialog.value,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        TwoButtonDialog(
            titleText = "마이크 권한이 필요합니다.",
            messageText = "세팅화면에서 마이크 권한을 ON 해주세요.",
            endButtonText = "세팅으로 가기",
            cancelButtonText = "취소",
            onLeftButtonClick = {
                model.isRecordingPermissionDialog.value = false
                openAppSettings(context)
            },
            onRightButtonClick = { model.isRecordingPermissionDialog.value = false },
            onBackHandler = { model.isRecordingPermissionDialog.value = false }
        )
    }
    FadeView(
        visible = isTranslationOverlayVisible.value,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        TranslationOverlayView()
    }
    AnimatedVisibility(
        visible = isAiSettingVisible.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier.fillMaxSize()
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
        fontFamily = MainFont.Pretendard,
        fontSize = 18.textDp,
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
            fontSize = 14.textDp,
            fontFamily = MainFont.Pretendard,
            lineHeight = 19.textDp,
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
        fontFamily = MainFont.Pretendard,
        color = Color(0xFFFFFFFF)
    )
}

@Preview
@Composable
private fun CallPreview() {
    CallView()
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}