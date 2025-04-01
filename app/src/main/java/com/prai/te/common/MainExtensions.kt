package com.prai.te.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
internal fun Modifier.cleanClickable(onClick: () -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }

    return clickable(interactionSource = source, indication = null) {
        onClick.invoke()
    }
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

internal val Int.textDp: TextUnit
    @Composable get() = with(LocalDensity.current) { this@textDp.dp.toSp() }