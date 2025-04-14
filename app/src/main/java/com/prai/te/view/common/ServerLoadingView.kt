package com.prai.te.view.common

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
internal fun ServerLoadingView(delay: Long) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        if (isVisible) {
            CubeGrid(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun CubeGrid(
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(40.dp, 40.dp),
    durationMillis: Int = 1000,
    color: Color = Color(0xFFFFFFFF),
) {
    val transition = rememberInfiniteTransition()

    val durationPerFraction = durationMillis / 2

    val rectSizeMultiplier1 = transition.fractionTransition(
        initialValue = 1f,
        targetValue = 0f,
        durationMillis = durationPerFraction,
        delayMillis = durationMillis / 4,
        repeatMode = RepeatMode.Reverse,
        easing = EaseInOut
    )
    val rectSizeMultiplier2 = transition.fractionTransition(
        initialValue = 1f,
        targetValue = 0f,
        durationMillis = durationPerFraction,
        offsetMillis = durationPerFraction / 4,
        delayMillis = durationMillis / 4,
        repeatMode = RepeatMode.Reverse,
        easing = EaseInOut
    )
    val rectSizeMultiplier3 = transition.fractionTransition(
        initialValue = 1f,
        targetValue = 0f,
        durationMillis = durationPerFraction,
        offsetMillis = durationPerFraction / 4 * 2,
        delayMillis = durationMillis / 4,
        repeatMode = RepeatMode.Reverse,
        easing = EaseInOut
    )
    val rectSizeMultiplier4 = transition.fractionTransition(
        initialValue = 1f,
        targetValue = 0f,
        durationMillis = durationPerFraction,
        offsetMillis = durationPerFraction / 4 * 3,
        delayMillis = durationMillis / 4,
        repeatMode = RepeatMode.Reverse,
        easing = EaseInOut
    )
    val rectSizeMultiplier5 = transition.fractionTransition(
        initialValue = 1f,
        targetValue = 0f,
        durationMillis = durationPerFraction,
        offsetMillis = durationPerFraction / 4 * 4,
        delayMillis = durationMillis / 4,
        repeatMode = RepeatMode.Reverse,
        easing = EaseInOut
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier3.value),
                    color = color
                ) {

                }
            }
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier4.value),
                    color = color
                ) {

                }
            }
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier5.value),
                    color = color
                ) {

                }
            }
        }
        Row {
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier2.value),
                    color = color
                ) {

                }
            }
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier3.value),
                    color = color
                ) {

                }
            }
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier4.value),
                    color = color
                ) {

                }
            }
        }
        Row {
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier1.value),
                    color = color
                ) {

                }
            }
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier2.value),
                    color = color
                ) {

                }
            }
            Box(modifier = Modifier.size(size / 3), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(size / 3 * rectSizeMultiplier3.value),
                    color = color
                ) {

                }
            }
        }
    }
}

@Composable
internal fun InfiniteTransition.fractionTransition(
    initialValue: Float,
    targetValue: Float,
    fraction: Int = 1,
    durationMillis: Int,
    delayMillis: Int = 0,
    offsetMillis: Int = 0,
    repeatMode: RepeatMode = RepeatMode.Restart,
    easing: Easing = FastOutSlowInEasing
): State<Float> {
    return animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                this.durationMillis = durationMillis
                this.delayMillis = delayMillis
                initialValue at 0 with easing
                when (fraction) {
                    1 -> {
                        targetValue at durationMillis with easing
                    }

                    2 -> {
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue at durationMillis with easing
                    }

                    3 -> {
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue / fraction * 2 at durationMillis / fraction * 2 with easing
                        targetValue at durationMillis with easing
                    }

                    4 -> {
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue / fraction * 2 at durationMillis / fraction * 2 with easing
                        targetValue / fraction * 3 at durationMillis / fraction * 3 with easing
                        targetValue at durationMillis with easing
                    }
                }
            },
            repeatMode,
            StartOffset(offsetMillis)
        )
    )
}