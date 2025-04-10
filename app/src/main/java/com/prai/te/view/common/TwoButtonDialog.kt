package com.prai.te.view.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prai.te.common.cleanClickable
import com.prai.te.common.clickBlocker

@Preview(widthDp = 600, heightDp = 1000)
@Composable
internal fun TwoButtonDialog(
    titleText: String = "오늘 대화는 여기까지 할까요?",
    messageText: String = "오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.",
    endButtonText: String = "더 써볼래요",
    cancelButtonText: String = "취소",
    onEndClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onBackHandler: () -> Unit = {}
) {
    BackHandler {
        onBackHandler()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickBlocker(true)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(20.dp)
                .wrapContentWidth()
                .wrapContentHeight()
                .background(color = Color(0xFF222222), shape = RoundedCornerShape(size = 16.dp))
        ) {
            Text(
                text = titleText,
                fontSize = 18.sp,
                lineHeight = 23.4.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 30.dp, bottom = 16.dp)
            )
            Text(
                text = messageText,
                fontSize = 16.sp,
                lineHeight = 20.8.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .padding(
                        top = 26.dp, bottom = 20.dp, start = 20.dp, end = 20.dp
                    )
            ) {
                Text(
                    text = endButtonText,
                    fontSize = 18.sp,
                    lineHeight = 23.4.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .cleanClickable { onEndClick() }
                        .weight(1f)
                        .background(
                            color = Color(0xFFFFCF31),
                            shape = RoundedCornerShape(size = 60.dp)
                        )
                        .padding(top = 16.dp, bottom = 16.dp)
                )
                Text(
                    text = cancelButtonText,
                    fontSize = 18.sp,
                    lineHeight = 23.4.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFCF31),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .cleanClickable { onCancelClick() }
                        .weight(1f)
                        .border(
                            width = 0.5.dp,
                            color = Color(0xFFFFCF31),
                            shape = RoundedCornerShape(size = 60.dp)
                        )
                        .padding(top = 16.dp,  bottom = 16.dp)
                )
            }
        }
    }
}