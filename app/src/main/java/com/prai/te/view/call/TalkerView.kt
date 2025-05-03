package com.prai.te.view.call

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.VideoView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.penfeizhou.animation.apng.APNGDrawable
import com.github.penfeizhou.animation.loader.ResourceStreamLoader
import com.prai.te.R
import com.prai.te.common.FadeView
import com.prai.te.common.cleanClickable
import com.prai.te.model.MainCallState
import com.prai.te.view.model.CallSegmentItem
import com.prai.te.view.model.MainViewModel
import kotlinx.coroutines.delay

@Composable
internal fun TalkerBox(
    segment: CallSegmentItem?,
    state: MainCallState,
    modifier: Modifier,
    model: MainViewModel = viewModel()
) {
    val imageSize = 150.dp
    val initialRadius = with(LocalDensity.current) { imageSize.toPx() / 2 - 10f }

    // Video와 Image의 alpha 애니메이션
    val videoAlpha = remember { Animatable(if (state is MainCallState.Active) 1f else 0f) }
    val imageAlpha = remember { Animatable(if (state is MainCallState.Active) 0f else 1f) }

    // state 변경 시 alpha 애니메이션
    LaunchedEffect(state) {
        if (state is MainCallState.Active) {
            videoAlpha.animateTo(1f, animationSpec = tween(100, easing = LinearEasing))
            imageAlpha.animateTo(0f, animationSpec = tween(100, easing = LinearEasing))
        } else {
            videoAlpha.animateTo(0f, animationSpec = tween(100, easing = LinearEasing))
            imageAlpha.animateTo(1f, animationSpec = tween(100, easing = LinearEasing))
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .cleanClickable {
                // 주석 처리된 기존 로직 유지
//                if (model.callState.value == MainCallState.Connecting) {
//                    model.callState.value = MainCallState.Active("")
//                } else {
//                    model.callState.value = MainCallState.Connecting
//                }
            }
            .size(383.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,


            modifier = Modifier.size(180.dp)
        ) {
            VideoLoopScreen(state = state, alpha = videoAlpha.value, videoAlphaAnimatable = videoAlpha)
            Image(
                painter = painterResource(R.drawable.main_talker_waiting),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .alpha(imageAlpha.value)
            )
        }
        if (state != MainCallState.None) {
            GrowingCircle(segment, state, initialRadius = initialRadius, initialDelay = 0)
            GrowingCircle(segment, state, initialRadius = initialRadius, initialDelay = 1000)
            GrowingCircle(segment, state, initialRadius = initialRadius, initialDelay = 2000)
        }
    }
}

@Composable
private fun GrowingCircle(
    segment: CallSegmentItem?,
    state: MainCallState,
    initialRadius: Float = 0f,
    initialDelay: Int = 0
) {
    val strokeWidth = with(LocalDensity.current) { 1.dp.toPx() }
    val maxRadius = with(LocalDensity.current) { 180.dp.toPx() }
    val radius = remember { Animatable(initialRadius) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    val isVisible by remember(segment, state, lifecycleState) {
        derivedStateOf {
            (segment != null || state == MainCallState.Connecting) &&
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

@Composable
private fun VideoLoopScreen(state: MainCallState, alpha: Float, videoAlphaAnimatable: Animatable<Float, *>) {
    val context = LocalContext.current
    val videoUri = "android.resource://${context.packageName}/raw/main_talker_video".toUri()

    // 생명주기 상태 추적
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    // VideoView를 단일 인스턴스로 유지
    val videoView = remember {
        VideoView(context).apply {
            setVideoURI(videoUri)
            setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
            }
        }
    }

    // 생명주기와 상태에 따른 비디오 재생 제어
    LaunchedEffect(lifecycleState, state) {
        // 앱이 포그라운드에 있고 Active 상태일 때만 재생
        if (lifecycleState.isAtLeast(Lifecycle.State.RESUMED) && state is MainCallState.Active) {
            if (!videoView.isPlaying) {
                videoView.start()
            }
        } else {
            // 그 외의 경우 일시 중지
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }
    }

    AndroidView(
        modifier = Modifier
            .size(180.dp)
            .alpha(alpha),
        factory = { videoView },
        update = { /* 업데이트 로직은 LaunchedEffect에서 처리 */ }
    )

    // 리소스 정리
    DisposableEffect(Unit) {
        onDispose {
            videoView.stopPlayback()
        }
    }
}