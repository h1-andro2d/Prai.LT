package com.prai.te.view

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prai.te.common.MainCodec
import com.prai.te.media.MainFileManager
import com.prai.te.media.MainPlayer
import com.prai.te.media.MockAudio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay

fun Modifier.shimmerLoadingAnimation(
    isLoadingCompleted: Boolean = true, // <-- New parameter for start/stop.
    isLightModeActive: Boolean = true, // <-- New parameter for display modes.
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
): Modifier {
    if (isLoadingCompleted) { // <-- Step 1.
        return this
    }
    else {
        return composed {
            // Step 2.
            val shimmerColors = ShimmerAnimationData(isLightMode = isLightModeActive).getColours()
            val transition = rememberInfiniteTransition(label = "")
            val translateAnimation = transition.animateFloat(
                initialValue = 0f,
                targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = durationMillis,
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "Shimmer loading animation",
            )
            this.background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                    end = Offset(x = translateAnimation.value, y = angleOfAxisY),
                ),
            )
        }
    }
}
data class ShimmerAnimationData(
    private val isLightMode: Boolean
) {
    fun getColours(): List<Color> {
        return if (isLightMode) {
            val color = Color.White
            listOf(
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 1.0f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f),
            )
        } else {
            val color = Color.Black
            listOf(
                color.copy(alpha = 0.0f),
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.0f),
            )
        }
    }
}

@Preview
@Composable
fun HomeScreen() {
    var isLoadingCompleted by remember { mutableStateOf(false) }
    var isLightModeActive by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isLightModeActive) Color.White else Color.Black)
            .border(border = BorderStroke(width = 4.dp, color = Color.Black))
            .padding(48.dp)
    ) {
        Column(
            modifier = Modifier.align(alignment = Alignment.TopCenter)
        ) {
            Column {
                ComponentRectangle(isLoadingCompleted, isLightModeActive)
                Spacer(modifier = Modifier.padding(8.dp))
                ComponentRectangleLineLong(isLoadingCompleted, isLightModeActive)
                Spacer(modifier = Modifier.padding(4.dp))
                ComponentRectangleLineShort(isLoadingCompleted, isLightModeActive)
            }
            Spacer(modifier = Modifier.padding(24.dp))
            Row {
                ComponentCircle(isLoadingCompleted, isLightModeActive)
                Spacer(modifier = Modifier.padding(4.dp))
                Column {
                    Spacer(modifier = Modifier.padding(8.dp))
                    ComponentRectangleLineLong(isLoadingCompleted, isLightModeActive)
                    Spacer(modifier = Modifier.padding(4.dp))
                    ComponentRectangleLineShort(isLoadingCompleted, isLightModeActive)
                }
            }
            Spacer(modifier = Modifier.padding(24.dp))
            Row {
                ComponentSquare(isLoadingCompleted, isLightModeActive)
                Spacer(modifier = Modifier.padding(4.dp))
                Column {
                    Spacer(modifier = Modifier.padding(8.dp))
                    ComponentRectangleLineLong(isLoadingCompleted, isLightModeActive)
                    Spacer(modifier = Modifier.padding(4.dp))
                    ComponentRectangleLineShort(isLoadingCompleted, isLightModeActive,)
                }
            }
        }
    }
}
@Composable
fun ComponentCircle(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .background(color = Color.LightGray, shape = CircleShape)
            .size(100.dp)
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentSquare(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(24.dp))
            .background(color = Color.LightGray)
            .size(100.dp)
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentRectangle(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(24.dp))
            .background(color = Color.LightGray)
            .height(200.dp)
            .fillMaxWidth()
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentRectangleLineLong(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .size(height = 30.dp, width = 200.dp)
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}
@Composable
fun ComponentRectangleLineShort(
    isLoadingCompleted: Boolean,
    isLightModeActive: Boolean,
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .size(height = 30.dp, width = 100.dp)
            .shimmerLoadingAnimation(isLoadingCompleted, isLightModeActive)
    )
}

@Preview
@Composable
fun LoadingDotAnimation(
    modifier: Modifier = Modifier,
    circleSize: Dp = 10.dp,
    circleColor: Color = Color(0xCCFFCF31),
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 30.dp
) {
    val circles = remember {
        List(3) { Animatable(initialValue = 0f) }
    }

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1200 with LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }
    val lastCircle = circleValues.size - 1

    Row(modifier = modifier) {
        circleValues.forEachIndexed { index, value ->
            Box(modifier = Modifier
                .size(circleSize)
                .graphicsLayer { translationY = -value * distance }
                .background(color = circleColor, shape = CircleShape)
            )
            if (index != lastCircle) Spacer(modifier = Modifier.width(spaceBetween))
        }
    }
}

@Preview
@Composable
fun AlphaAnimationText() {
    // Remember the infinite transition
    val infiniteTransition = rememberInfiniteTransition()

    // Create an animated float for alpha
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Text with animated alpha
    Text(
        text = "대화내용을 생성하고 있습니다",
        fontSize = 15.sp,
        color = Color.White.copy(alpha = alpha),
        modifier = Modifier.padding(16.dp)
    )
}

@Preview
@Composable
fun LoadingDotAnimation2(
    modifier: Modifier = Modifier,
    circleSize: Dp = 10.dp,
    circleColor: Color = Color(0xCCFFCF31),
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 15.dp
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(300L)
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 using LinearOutSlowInEasing
                        1.0f at 300 using LinearOutSlowInEasing
                        0.0f at 600 using LinearOutSlowInEasing
                        0.0f at 1200 using LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }
    val gend = with(LocalDensity.current) { circleSize.toPx() }
    val lastCircle = circleValues.size - 1

    Row(modifier = modifier) {
        circleValues.forEachIndexed { index, value ->
            val gradient = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF).copy(alpha = 0.8f), circleColor, circleColor.copy(alpha = 0.4f)),
                start = Offset(0f, 0f),
                end = Offset(0f, gend)
            )

            Box(modifier = Modifier
                .size(circleSize)
                .graphicsLayer { translationY = -value * distance }
                .background(brush = gradient, shape = CircleShape)
            )
            if (index != lastCircle) Spacer(modifier = Modifier.width(spaceBetween))
        }
    }
}


/**
 * Pulsation animation that duplicates content and animate it under it.
 * @param enabled - if true uses the given parameters and start the animation.
 * @param type - set of animation params to be used for animated content
 * @param content - composable content that will be drawn and copied for animation.
 */
@Composable
public fun Pulsation(
    enabled: Boolean,
    type: PulsationType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Pulsation(
        enabled = enabled,
        repeatsCount = type.repeatsCount,
        iterations = type.iterations,
        iterationDuration = type.iterationDuration,
        iterationDelay = type.iterationDelay,
        delayBetweenRepeats = type.delayBetweenRepeats,
        alphaRange = type.alphaRange,
        pulseRange = type.pulseRange,
        contentType = type.contentType,
        wavesCount = type.wavesCount,
        modifier = modifier,
        content = content
    )
}


@Preview
@Composable
private fun test() {
    Pulsation(
        enabled = true,
        type = PulsationType.Races(
            duration = 2500,
            contentType = ContentType.Colored(Color.Green, CircleShape)
        )
    ) {
        Box(
            modifier = Modifier
                .background(Color.Yellow, shape = CircleShape)
                .size(124.dp)
        )
    }
}
/**
 * Pulsation animation that duplicates content and animate it under it.
 * Core function that gives more options to modify.
 * @param enabled - if true uses the given parameters and start the animation.
 * @param repeatsCount - amount of animation repeats. Use [Int.MAX_VALUE] for infinite repeats
 * @param delayBetweenRepeats - milliseconds between each repeat of animation cycle.
 * @param iterations - a cycle of repetitive animations.
 * @param iterationDuration - duration of 1 animation.
 * @param iterationDelay - delay between iterations in 1 animation cycle.
 * @param pulseRange - range between minimum and maximum animated [content] scale.
 * @param alphaRange - range between started and ended alpha for animated [content].
 * @param contentType - type of animated view.
 * @param content - composable content that will be drawn and copied for animation.
 */
@Composable
public fun Pulsation(
    enabled: Boolean,
    repeatsCount: Int,
    delayBetweenRepeats: Int,
    iterations: Int,
    iterationDuration: Int,
    iterationDelay: Int,
    wavesCount: Int,
    modifier: Modifier = Modifier,
    pulseRange: ClosedFloatingPointRange<Float>,
    alphaRange: ClosedFloatingPointRange<Float>,
    contentType: ContentType,
    content: @Composable () -> Unit
) {
    val animationHolder = remember {
        List(wavesCount) {
            AnimationHolder(
                scale = Animatable(pulseRange.start),
                alpha = Animatable(alphaRange.start)
            )
        }
    }
    LaunchedEffect(animationHolder, enabled) {
        while (enabled) {
            var count = 0
            animationHolder.flatMapIndexed { index, (scale, alpha) ->
                listOf(
                    async {
                        delay((index * (iterationDuration / wavesCount)).toLong())
                        scale.snapTo(pulseRange.start)
                        scale.animateTo(
                            targetValue = pulseRange.endInclusive,
                            animationSpec = repeatable(
                                iterations = iterations,
                                animation = tween(
                                    durationMillis = iterationDuration,
                                    delayMillis =  iterationDelay
                                ),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        scale.snapTo(pulseRange.start)
                    },
                    async {
                        delay((index * (iterationDuration / wavesCount)).toLong())
                        alpha.snapTo(alphaRange.start)
                        alpha.animateTo(
                            targetValue = alphaRange.endInclusive,
                            animationSpec = repeatable(
                                iterations = iterations,
                                animation = tween(
                                    durationMillis = iterationDuration,
                                    delayMillis = iterationDelay
                                ),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                    }
                )
            }.awaitAll()
            count += 1
            if (repeatsCount <= count) {
                return@LaunchedEffect
            }
            delay(delayBetweenRepeats.toLong())
        }
        animationHolder.forEach { (scale, alpha) ->
            scale.snapTo(pulseRange.start)
            alpha.snapTo(alphaRange.start)
        }
    }
    var size: IntSize by remember {
        mutableStateOf(IntSize(0, 0))
    }
    val dpSize = with(LocalDensity.current) {
        DpSize(width = size.width.toDp(), height = size.height.toDp())
    }
    Box(modifier = modifier) {
        animationHolder.forEachIndexed { _, (scale, alpha) ->
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            ) {
                when (contentType) {
                    is ContentType.Colored -> {
                        Box(
                            modifier = Modifier
                                .size(dpSize)
                                .background(contentType.color, shape = contentType.shape)
                        )
                    }

                    ContentType.ContentTwin -> content()
                    is ContentType.Gradient -> {
                        Box(
                            modifier = Modifier
                                .size(dpSize)
                                .background(contentType.brush, shape = contentType.shape)
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.onGloballyPositioned {
            size = it.size
        }) {
            content()
        }
    }
}

/**
 * Top level declaration of animation type for simplified usages of animation params
 */
public sealed class PulsationType(
    public val repeatsCount: Int,
    public val iterations: Int,
    public val iterationDuration: Int,
    public val iterationDelay: Int,
    public val delayBetweenRepeats: Int,
    public val contentType: ContentType,
    public val wavesCount: Int = 1,
    public val pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
    public val alphaRange: ClosedFloatingPointRange<Float> = 1f..0f,
) {
    /**
     * Linear animation type
     */
    public class Linear(
        repeatsCount: Int = Int.MAX_VALUE,
        duration: Int = 500,
        delayBetweenRepeats: Int = 0,
        contentType: ContentType = ContentType.ContentTwin,
        pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
        alphaRange: ClosedFloatingPointRange<Float> = 1f..0f,
    ) : PulsationType(
        repeatsCount = repeatsCount,
        iterations = 1,
        iterationDuration = duration,
        iterationDelay = 0,
        delayBetweenRepeats = delayBetweenRepeats,
        pulseRange = pulseRange,
        contentType = contentType,
        alphaRange = alphaRange
    )

    /**
     * Added possibility to make animation cycles inside animation process.
     */
    public class Iterative(
        repeatCount: Int = Int.MAX_VALUE,
        iterations: Int = 3,
        iterationDuration: Int = 500,
        iterationDelay: Int = 0,
        delayBetweenRepeats: Int = 500,
        contentType: ContentType = ContentType.ContentTwin,
        pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
        alphaRange: ClosedFloatingPointRange<Float> = 1f..0f,
    ) : PulsationType(
        repeatsCount = repeatCount,
        iterations = iterations,
        iterationDuration = iterationDuration,
        iterationDelay = iterationDelay,
        delayBetweenRepeats = delayBetweenRepeats,
        pulseRange = pulseRange,
        alphaRange = alphaRange,
        contentType = contentType,
    )

    /**
     * Start of next animations before end of previous
     */
    public class Races(
        duration: Int = 500,
        wavesCount: Int = 5,
        contentType: ContentType = ContentType.ContentTwin,
        pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
        alphaRange: ClosedFloatingPointRange<Float> = 1f .. 0f,
    ) : PulsationType(
        repeatsCount = Int.MAX_VALUE,
        iterations = 1,
        iterationDuration = duration,
        iterationDelay = 0,//duration.div(wavesCount),
        wavesCount = wavesCount,
        delayBetweenRepeats = 0,
        pulseRange = pulseRange,
        alphaRange = alphaRange,
        contentType = contentType,
    )
}

/**
 * Adds possibility to change animated pulsation background object
 */
public sealed interface ContentType {

    /**
     * Creates animated object with defined color and shape.
     */
    public class Colored(public val color: Color, public val shape: Shape = RectangleShape) : ContentType

    /**
     * Creates animated object with defined brush and shape
     */
    public class Gradient(public val brush: Brush, public val shape: Shape = RectangleShape) : ContentType

    /**
     * Creates the same animated object as a given content
     */
    public data object ContentTwin : ContentType
}

internal data class AnimationHolder(
    val scale: Animatable<Float, AnimationVector1D>,
    val alpha: Animatable<Float, AnimationVector1D>
)

@Preview
@Composable
private fun TestCode() {
    val context = LocalContext.current
    val player = MainPlayer(CoroutineScope(Dispatchers.IO + SupervisorJob()))
    val path = MainFileManager.createAudioFilePath(context)
    MainCodec.decodeBase64ToFile(MockAudio.data, path)
    Log.d("MainLogger", "${player.getDuration(path)}")
    player.start(path)
}