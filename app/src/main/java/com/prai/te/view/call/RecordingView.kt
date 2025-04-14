package com.prai.te.view.call

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.view.model.MainViewModel
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun RecordingView(model: MainViewModel = viewModel()) {
    val seconds = model.recordTime.collectAsStateWithLifecycle()
    val minutes = seconds.value / 60
    val remainingSeconds = seconds.value % 60

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .width(281.dp)
            .height(55.dp)
            .background(color = Color(0x66555555), shape = RoundedCornerShape(12.dp))
            .padding(top = 12.dp, start = 20.dp, end = 15.dp, bottom = 12.dp)
    ) {
        SmoothMicVolumeVisualizer()
        Text(
            text = String.format(
                LocalConfiguration.current.locales.get(0),
                "%02d:%02d",
                minutes,
                remainingSeconds
            ),
            fontSize = 12.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(500),
            color = Color(0xFFFFFFFF),
            modifier = Modifier.padding(start = 17.dp, end = 20.dp)
        )
        Image(
            painter = painterResource(R.drawable.main_redcording_x),
            contentDescription = null,
            modifier = Modifier
                .cleanClickable { model.cancelRecording() }
                .size(30.dp)
        )
    }
}

@Composable
private fun SmoothMicVolumeVisualizer(
    barCount: Int = 30,
    barWidth: Float = 1.5f,
    spaceBetween: Float = 3f,
    updateIntervalMillis: Long = 200L,
    animationDurationMillis: Int = 400,
    modifier: Modifier = Modifier.size(width = 146.dp, height = 31.dp),
    model: MainViewModel = viewModel()
) {
    val targetValues = remember {
        List(barCount) { mutableFloatStateOf(0f) }
    }
    val volume = model.volumeLevel.collectAsStateWithLifecycle()

    LaunchedEffect(volume) {
        while (isActive) {
            targetValues.forEach { mutableValue ->
                mutableValue.floatValue =
                    (Random.nextFloat() * volume.value).coerceAtLeast(volume.value * 0.2f)
            }
            delay(updateIntervalMillis)
        }
    }

    val currentHeights = targetValues.map { target ->
        animateFloatAsState(
            targetValue = target.floatValue,
            animationSpec = tween(durationMillis = animationDurationMillis),
            label = "smooth_mic_volume_visualizer"
        ).value
    }

    Canvas(modifier = modifier) {
        val barWidthPx = barWidth.dp.toPx()
        val spaceBetweenPx = spaceBetween.dp.toPx()

        val totalBarsWidth = barCount * barWidthPx + (barCount - 1) * spaceBetweenPx
        val startX = (size.width - totalBarsWidth) / 2f

        val canvasHeight = size.height
        val centerY = canvasHeight / 2f

        currentHeights.forEachIndexed { index, fraction ->
            val barHeightPx = fraction * canvasHeight

            val top = centerY - (barHeightPx / 2)
            val left = startX + index * (barWidthPx + spaceBetweenPx)
            val barSize = Size(width = barWidthPx, height = barHeightPx)

            drawRoundRect(
                color = Color(0xFFFFCF31),
                topLeft = Offset(left, top),
                size = barSize,
                cornerRadius = CornerRadius(
                    x = barWidthPx / 2,
                    y = barWidthPx / 2
                )
            )
        }
    }
}