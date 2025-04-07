package com.prai.te.view.call

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.penfeizhou.animation.apng.APNGDrawable
import com.github.penfeizhou.animation.loader.ResourceStreamLoader
import com.prai.te.R
import com.prai.te.common.FadeView
import com.prai.te.model.MainCallState
import com.prai.te.view.model.MainViewModel
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
            GrowingCircle(state, initialRadius = initialRadius, initialDelay = 0)
            GrowingCircle(state, initialRadius = initialRadius, initialDelay = 1000)
            GrowingCircle(state, initialRadius = initialRadius, initialDelay = 2000)
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
private fun GrowingCircle(
    state: MainCallState,
    initialRadius: Float = 0f,
    initialDelay: Int = 0,
    model: MainViewModel = viewModel()
) {
    val strokeWidth = with(LocalDensity.current) { 1.dp.toPx() }
    val maxRadius = with(LocalDensity.current) { 180.dp.toPx() }
    val radius = remember { Animatable(initialRadius) }
    val segment = model.currentSegment.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    val isVisible by remember(segment.value, state, lifecycleState) {
        derivedStateOf {
            (segment.value != null || state == MainCallState.Connecting) &&
                    lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            radius.snapTo(initialRadius)
            delay(initialDelay.toLong())
            while (isVisible) {
                radius.animateTo(
                    targetValue = maxRadius,
                    animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
                )
                radius.snapTo(initialRadius)
            }
        }
    }

    FadeView(isVisible) {
        Canvas(modifier = Modifier.size(383.dp)) {
            drawCircle(
                alpha = (maxRadius - radius.value) / maxRadius,
                color = Color(0xFFFFCF31),
                radius = radius.value,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

@Preview
@Composable
private fun ImageViewPreview() {
    LocalApngImage(R.raw.image)
}

@Composable
private fun LocalApngImage(resourceId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            ImageView(context).apply {
                val loader = ResourceStreamLoader(context, resourceId)
                val drawable = APNGDrawable(loader).apply {
                    setAutoPlay(true)
                }
                setImageDrawable(drawable)
            }
        }
    )
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
    val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1

    val bitmap = createBitmap(width, height)
    val canvas = android.graphics.Canvas(bitmap)

    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}