package com.prai.te.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun Modifier.cleanClickable(onClick: () -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }

    return clickable(interactionSource = source, indication = null) {
        onClick.invoke()
    }
}


@Composable
internal fun Modifier.clickBlocker(shouldBlock: Boolean): Modifier {
    if (shouldBlock.not()) {
        return this
    }
    return cleanClickable { }
}

internal fun Modifier.clearFocusCleanClickable(
    lazy: Boolean = false,
    action: () -> Unit = {}
): Modifier = then(
    Modifier.composed {
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current
        val controller = LocalSoftwareKeyboardController.current
        Modifier.cleanClickable {
            scope.launch {
                if (lazy.not()) {
                    action.invoke()
                }
                controller?.hide()
                delay(300L)
                focusManager.clearFocus()
                if (lazy) {
                    action.invoke()
                }
            }
        }
    }
)

internal fun Modifier.clearFocusRippleClickable(
    lazy: Boolean = false,
    action: () -> Unit = {}
): Modifier = then(
    Modifier.composed {
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current
        val controller = LocalSoftwareKeyboardController.current
        Modifier.clearFocusClickable {
            scope.launch {
                if (lazy.not()) {
                    action.invoke()
                }
                controller?.hide()
                delay(300L)
                focusManager.clearFocus()
                if (lazy) {
                    action.invoke()
                }
            }
        }
    }
)


internal fun Modifier.clearFocusClickable(
    lazy: Boolean = false,
    action: () -> Unit = {}
): Modifier = then(
    Modifier.composed {
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current
        val controller = LocalSoftwareKeyboardController.current
        Modifier.clickable {
            scope.launch {
                if (lazy.not()) {
                    action.invoke()
                }
                controller?.hide()
                delay(300L)
                focusManager.clearFocus()
                if (lazy) {
                    action.invoke()
                }
            }
        }
    }
)

@Composable
internal fun Modifier.rippleClickable(
    color: Color = MainColor.Greyscale19WH,
    onClick: () -> Unit
): Modifier {
    return clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = color)
    ) {
        onClick.invoke()
    }
}

@Composable
internal fun VerticalGap(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
internal fun FadeView(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        content.invoke()
    }
}

@Composable
internal fun CutFadeView(
    visible: Boolean,
    modifier: Modifier = Modifier,
    cutStart: Boolean = false,
    cutEnd: Boolean = false,
    content: @Composable () -> Unit
) {
    val enterDuration = if (cutStart) 0 else 700
    val exitDuration = if (cutEnd) 0 else 700

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(enterDuration)),
        exit = fadeOut(tween(exitDuration)),
        modifier = modifier
    ) {
        content.invoke()
    }
}

internal val Int.textDp: TextUnit
    @Composable get() = with(LocalDensity.current) { this@textDp.dp.toSp() }
