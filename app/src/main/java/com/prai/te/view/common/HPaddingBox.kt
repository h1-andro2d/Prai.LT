package com.prai.te.view.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun HPaddingBox(padding: Int, content: @Composable (BoxScope) -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = padding.dp)
            .fillMaxWidth()
    ) {
        content.invoke(this)
    }
}