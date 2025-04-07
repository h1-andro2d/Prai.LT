package com.prai.te.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
internal fun MainSaveButton(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = "저장",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color(0xFF121212),
            fontWeight = FontWeight.W600,
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 20.dp)
                .fillMaxWidth()
                .height(53.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(60.dp))
                .padding(vertical = 14.dp)
        )
    }
}