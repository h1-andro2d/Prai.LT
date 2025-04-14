package com.prai.te.view.intro

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.MainNavigator
import com.prai.te.common.cleanClickable
import com.prai.te.common.rippleClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainEvent
import com.prai.te.model.MainIntroState
import com.prai.te.view.model.MainViewModel

@Preview
@Composable
internal fun LoginView(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current

    BackHandler {
        viewModel.introEndDialog.value = true
    }

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        Column(modifier = Modifier.padding(start = 40.dp, top = 68.dp)) {
            Text(
                text = "전화하듯,\n친구처럼,\n영어가 자연스러워지는 경험",
                fontSize = 26.textDp,
                lineHeight = 33.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
            )
            Row {
                Text(
                    text = "PRAI",
                    fontSize = 42.textDp,
                    lineHeight = 55.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.padding(top = 23.dp)
                )
                Spacer(
                    modifier = Modifier
                        .padding(start = 4.dp, top = 28.dp)
                        .size(12.dp)
                        .background(color = MainColor.PrimaryYE, shape = CircleShape)
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 101.dp)
                .rippleClickable(MainColor.Greyscale12WH) {
                    viewModel.dispatchEvent(MainEvent.GoogleLoginRequest)
                }
                .clipToBounds()
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(size = 60.dp)
                )
        ) {
            Row(
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(R.drawable.main_icon_google),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(20.dp)
                )
                Text(
                    text = "Google로 계속하기",
                    fontSize = 16.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(600),
                )
            }
        }
        Text(
            text = "로그인함으로써 PRAI 서비스 이용을 위해",
            fontSize = 14.textDp,
            lineHeight = 19.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale15WH,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 54.dp)
                .align(Alignment.BottomCenter)
        )
        Row(
            modifier = Modifier
                .padding(bottom = 35.dp)
                .align(Alignment.BottomCenter)
        ) {
            NormalText("필요한 ")
            HyperLinkText("개인 정보 처리 방침") {
                MainNavigator.startPrivacyWebView(context)
            }
            NormalText("과 ")
            HyperLinkText("이용약관") {
                MainNavigator.startRuleWebView(context)
            }
            NormalText("에 동의하게 됩니다.")
        }
    }
}

@Composable
private fun NormalText(text: String) {
    Text(
        text = text,
        fontSize = 14.textDp,
        lineHeight = 19.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale15WH,
        textAlign = TextAlign.Center,
        modifier = Modifier
    )
}

@Composable
private fun HyperLinkText(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 14.textDp,
        lineHeight = 19.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale17WH,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.cleanClickable {
            onClick.invoke()
        }
    )
}