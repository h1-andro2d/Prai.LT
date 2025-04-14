package com.prai.te.view.call

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.FadeView
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainCallState
import com.prai.te.view.model.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Preview
@Composable
internal fun MenuRowView(
    isRecording: Boolean = true,
    state: MainCallState = MainCallState.Active("test"),
    modifier: Modifier = Modifier,
    model: MainViewModel = viewModel()
) {
    val callIconSize = 92.dp
    val callIconRootSize = 110.dp

    val micIconSize = 90.dp
    val micIconRootSize = 97.dp

    Box(
        modifier = modifier
            .padding(horizontal = 50.dp)
            .fillMaxWidth()
            .background(color = Color(0xFF000000))
    ) {
        FadeView(
            visible = state != MainCallState.Connecting,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(R.drawable.main_button_ai),
                contentDescription = null,
                modifier = Modifier
                    .cleanClickable { model.isAiSettingVisible.value = true }
                    .size(58.dp)
            )
        }
        Crossfade(
            targetState = state,
            label = "menu_row_cross_fade",
            modifier = Modifier.align(Alignment.Center)
        ) { state ->
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(callIconRootSize)) {
                when (state) {
                    is MainCallState.Connecting -> Unit
                    is MainCallState.None -> CallIconWithAnimation(callIconSize, callIconRootSize)
                    is MainCallState.Active -> MicIconWithAnimation(
                        isRecording,
                        micIconSize,
                        micIconRootSize
                    )
                }
            }
        }
        FadeView(
            visible = state != MainCallState.Connecting,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Crossfade(
                targetState = state,
                label = "main_right_button_cross_fade",
                modifier = Modifier.align(Alignment.Center)
            ) { state ->
                when (state) {
                    is MainCallState.Connecting -> Unit
                    is MainCallState.None -> {
                        Image(
                            painter = painterResource(R.drawable.main_button_chat),
                            contentDescription = null,
                            modifier = Modifier
                                .cleanClickable {
                                    model.openChatList()
                                }
                                .size(58.dp)
                        )
                    }

                    is MainCallState.Active -> {
                        Image(
                            painter = painterResource(R.drawable.main_button_menu),
                            contentDescription = null,
                            modifier = Modifier
                                .cleanClickable {
                                    model.isSettingOverlayVisible.value =
                                        model.isSettingOverlayVisible.value.not()
                                }
                                .size(58.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun MenuOverlayView(model: MainViewModel = viewModel()) {
    BackHandler {
        model.isSettingOverlayVisible.value = false
    }

    Box(
        modifier = Modifier
            .cleanClickable { }
            .fillMaxSize()
            .background(color = Color(0xB3000000))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .padding(bottom = 73.dp, end = 53.dp)
                .align(Alignment.BottomEnd)
        ) {
            OverlayTextIcon(text = "번역하기", painterResourceId = R.drawable.main_button_translate) {
                model.onTranslationStart()
            }
            OverlayTextIcon(text = "통화 종료", painterResourceId = R.drawable.main_button_end) {
                model.isSettingOverlayVisible.value = false
                model.isCallEndingDialog.value = true
            }
            Image(
                painter = painterResource(R.drawable.main_button_x),
                contentDescription = null,
                modifier = Modifier
                    .cleanClickable { model.isSettingOverlayVisible.value = false }
                    .size(54.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MicIconWithAnimation(
    isRecording: Boolean = true,
    iconSize: Dp = 90.dp,
    rootSize: Dp = 97.dp,
    model: MainViewModel = viewModel()
) {
    Crossfade(
        targetState = isRecording,
        label = "mic_cross_fade"
    ) { isRecording ->
        if (isRecording) {
            MicActiveIconWithAnimation(
                iconSize,
                rootSize,
                Modifier
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(rootSize)
            ) {
                Image(
                    painter = painterResource(R.drawable.main_mic_waiting),
                    contentDescription = null,
                    modifier = Modifier
                        .cleanClickable { model.sendRecordingRequest() }
                        .size(iconSize)
                )
            }
        }
    }
}

@Preview
@Composable
private fun MicActiveIconWithAnimation(
    iconSize: Dp = 90.dp,
    rootSize: Dp = 97.dp,
    modifier: Modifier = Modifier,
    model: MainViewModel = viewModel()
) {
    var progress by remember { mutableStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 57000,
            easing = LinearEasing
        )
    )

    LaunchedEffect(Unit) {
        progress = 1f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(rootSize)
            .background(color = Color(0xFF000000))
    ) {
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .size(rootSize)
                .padding(1.dp)
        ) {
            val strokeWidth = 2.dp.toPx()
            drawArc(
                color = Color(0xFFC2980E),
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

        }
        Image(
            painter = painterResource(R.drawable.main_mic_active),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .cleanClickable { model.stopRecording() }
                .size(iconSize)
        )
    }
}

@Composable
private fun CallIconWithAnimation(iconSize: Dp, rootSize: Dp, model: MainViewModel = viewModel()) {
    val duration = 2000L
    val sizeState = remember { mutableStateOf(iconSize) }
    val animatedSize = animateDpAsState(
        targetValue = sizeState.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration.toInt()),
            repeatMode = RepeatMode.Reverse
        ),
        label = "call_icon_animation"
    )
    LaunchedEffect(Unit) {
        while (isActive) {
            sizeState.value = iconSize - 1.dp
            delay(duration)
            sizeState.value = rootSize
            delay(duration)
        }
    }
    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .cleanClickable { model.onCallStart() }
                .size(rootSize)
        ) {
            drawCircle(
                color = Color(0xFF242D28),
                radius = animatedSize.value.toPx() / 2
            )
        }
        Image(
            painter = painterResource(R.drawable.main_button_call),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun OverlayTextIcon(
    text: String,
    @DrawableRes painterResourceId: Int,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            fontFamily = MainFont.Pretendard,
            fontSize = 16.textDp,
            lineHeight = 22.textDp,
            fontWeight = FontWeight(600),
            color = Color(0xFFFFFFFF),
            modifier = Modifier.padding(end = 9.dp)
        )
        Image(
            painter = painterResource(painterResourceId),
            contentDescription = null,
            modifier = Modifier
                .cleanClickable { onClick.invoke() }
                .size(54.dp)
        )
    }
}