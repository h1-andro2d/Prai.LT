package com.prai.te.view.call

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prai.te.R
import com.prai.te.model.MainCallState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun TalkerBox(state: MainCallState, modifier: Modifier) {
    val imageSize = 150.dp
    val initialRadius = with(LocalDensity.current) { imageSize.toPx() / 2 - 10f }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(383.dp)
    ) {
        if (state != MainCallState.None) {
            GrowingCircle(initialRadius = initialRadius, initialDelay = 0)
            GrowingCircle(initialRadius = initialRadius, initialDelay = 1000)
            GrowingCircle(initialRadius = initialRadius, initialDelay = 2000)
        }
        Crossfade(state, label = "talker_box_crosss_fade") { state ->
            when (state) {
                MainCallState.None,
                MainCallState.Connecting -> Image(
                    painter = painterResource(R.drawable.main_talker_waiting),
                    contentDescription = null,
                    modifier = Modifier.size(imageSize)
                )

                is MainCallState.Active -> Image(
                    painter = painterResource(R.drawable.main_talker_active),
                    contentDescription = null,
                    modifier = Modifier.size(imageSize)
                )
            }
        }
    }
}

@Composable
private fun GrowingCircle(initialRadius: Float = 0f, initialDelay: Int = 0) {
    val strokeWidth = with(LocalDensity.current) { 1.dp.toPx() }
    val maxRadius = with(LocalDensity.current) { 180.dp.toPx() }
    val radius = remember { Animatable(initialRadius) }

    LaunchedEffect(Unit) {
        delay(initialDelay.toLong())
        while (isActive) {
            radius.animateTo(
                targetValue = maxRadius,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
            radius.snapTo(initialRadius)
        }
    }

    Canvas(modifier = Modifier.size(383.dp)) {
        drawCircle(
            alpha = (maxRadius - radius.value) / maxRadius,
            color = Color(0xFFFFCF31),
            radius = radius.value,
            style = Stroke(width = strokeWidth)
        )
    }
}