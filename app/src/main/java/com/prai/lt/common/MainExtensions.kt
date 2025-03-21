package com.prai.lt.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.cleanClickable(onClick: () -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }

    return clickable(interactionSource = source, indication = null) {
        onClick.invoke()
    }
}