package com.prai.te.view.common.dialog

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
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.clickBlocker
import com.prai.te.common.textDp

@Preview(widthDp = 600, heightDp = 1000)
@Composable
internal fun TwoButtonDialog(
    titleText: String = "오늘 대화는 여기까지 할까요?",
    messageText: String = "오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.오늘도 멋지게 말하셨어요!\n다음 전화도 기대할게요.",
    endButtonText: String = "더 써볼래요",
    cancelButtonText: String = "취소",
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {},
    onBackHandler: () -> Unit = {},
    drawBackground: Boolean = false,
    reverseColor: Boolean = false
) {
    BackHandler {
        onBackHandler()
    }
    val backgroundModifier = if (drawBackground) {
        Modifier.background(color = Color(0xCC000000))
    } else {
        Modifier
    }

    val leftButtonColor = Modifier.background(
        color = Color(0xFFFFCF31),
        shape = RoundedCornerShape(size = 60.dp)
    )

    val rightButtonColor = Modifier.border(
        width = 0.5.dp,
        color = Color(0xFFFFCF31),
        shape = RoundedCornerShape(size = 60.dp)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = backgroundModifier
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
                fontSize = 18.textDp,
                lineHeight = 23.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 30.dp, bottom = 16.dp)
            )
            Text(
                text = messageText,
                fontSize = 16.textDp,
                fontFamily = MainFont.Pretendard,
                lineHeight = 20.textDp,
                fontWeight = FontWeight(400),
                color = Color(0xFFB7B7B7),
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
                    fontSize = 16.textDp,
                    fontFamily = MainFont.Pretendard,
                    lineHeight = 23.textDp,
                    fontWeight = FontWeight(400),
                    color = if (reverseColor.not()) {
                        Color(0xFF000000)
                    } else {
                        Color(0xFFFFCF31)
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .cleanClickable { onLeftButtonClick() }
                        .weight(1f)
                        .then(
                            if (reverseColor.not()) {
                                leftButtonColor
                            } else {
                                rightButtonColor
                            }
                        )
                        .padding(top = 16.dp, bottom = 16.dp)
                )
                Text(
                    text = cancelButtonText,
                    fontFamily = MainFont.Pretendard,
                    fontSize = 16.textDp,
                    lineHeight = 23.textDp,
                    fontWeight = FontWeight(400),
                    color = if (reverseColor.not()) {
                        Color(0xFFFFCF31)
                    } else {
                        Color(0xFF000000)
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .cleanClickable { onRightButtonClick() }
                        .weight(1f)
                        .then(
                            if (reverseColor.not()) {
                                rightButtonColor
                            } else {
                                leftButtonColor
                            }
                        )
                        .padding(top = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}