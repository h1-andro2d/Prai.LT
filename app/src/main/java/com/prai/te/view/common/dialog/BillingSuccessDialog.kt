package com.prai.te.view.common.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prai.te.R
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.clickBlocker
import com.prai.te.common.textDp

@Preview(widthDp = 600, heightDp = 1000)
@Composable
internal fun BillingSuccessDialog(
    titleText: String = "구독 완료!\nPRAI가 영어 파트너가 되어줄게요 :)",
    messageText: String = "이제 PRAI와 무제한으로 대화할 수 있어요.\n당신만의 영어 말하기 루틴,\nPRAI가 함께 만들어줄게요.\n오늘, 어떤 이야기부터 해볼까요?",
    updateButtonText: String = "PRAI랑 통화 하러 가기",
    onMainButtonClick: () -> Unit = {},
    onBackHandler: (() -> Unit) = {},
    drawBackground: Boolean = false
) {
    BackHandler { onBackHandler() }

    val modifier = if (drawBackground) {
        Modifier.background(color = Color(0xCC000000))
    } else {
        Modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickBlocker(true)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .background(color = Color(0xFF222222), shape = RoundedCornerShape(size = 16.dp))
            ) {
                Text(
                    text = titleText,
                    fontSize = 18.textDp,
                    fontFamily = MainFont.Pretendard,
                    lineHeight = 23.textDp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 110.dp, bottom = 16.dp)
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
                        text = updateButtonText,
                        fontSize = 16.textDp,
                        fontFamily = MainFont.Pretendard,
                        lineHeight = 23.textDp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .cleanClickable { onMainButtonClick() }
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFCF31),
                                shape = RoundedCornerShape(size = 60.dp)
                            )
                            .padding(start = 21.dp, top = 16.dp, end = 21.dp, bottom = 16.dp)
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.billing_success_icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(127.dp)
                    .height(114.dp)
            )
        }
    }
}