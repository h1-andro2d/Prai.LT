package com.prai.te.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp

@Preview
@Composable
internal fun CommonBackAndTitleHeader(title: String = "TEST", onBack: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.main_button_back),
            contentDescription = null,
            modifier = Modifier
                .cleanClickable { onBack.invoke() }
                .padding(start = 18.dp)
                .size(20.dp)
                .align(Alignment.CenterStart)
        )
        Text(
            text = title,
            fontSize = 18.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(600),
            color = MainColor.OnSurfaceWH,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}