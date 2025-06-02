package com.prai.te.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.textDp

@Preview
@Composable
internal fun CommonConfirmButton(
    text: String = "저장",
    enabled: Boolean = false,
    onClick: () -> Unit = {}
) {
    val color = if (enabled) {
        MainColor.PrimaryYE
    } else {
        MainColor.Greyscale09BK
    }
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 18.textDp,
        fontFamily = MainFont.Pretendard,
        color = Color(0xFF121212),
        fontWeight = FontWeight.W600,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(53.dp)
            .background(color = color, shape = RoundedCornerShape(60.dp))
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .clipToBounds()
            .padding(vertical = 14.dp)
    )
}
