package com.prai.te.view.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.MainNavigator
import com.prai.te.common.VerticalGap
import com.prai.te.common.cleanClickable
import com.prai.te.common.rippleClickable
import com.prai.te.common.textDp
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.model.UserGender

@Composable
internal fun MainSettingView(
    nameText: String,
    ageText: String,
    gender: UserGender?,
    model: MainViewModel = viewModel(),
) {
    val infoText = "${gender?.text ?: "UNKNOWN"} / ${ageText}년생"

    BackHandler { model.isMainSettingVisible.value = false }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_button_back),
                contentDescription = null,
                modifier = Modifier
                    .cleanClickable { model.isMainSettingVisible.value = false }
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(24.dp)
            )
            Text(
                text = "마이페이지",
                fontSize = 18.textDp,
                fontFamily = MainFont.Pretendard,
                lineHeight = 25.textDp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        VerticalGap(20)
        ProfileView()
        VerticalGap(22)
        NameText(nameText)
        VerticalGap(3)
        InfoText(infoText)
        VerticalGap(30)
        ItemBox()
    }

}

@Composable
private fun ProfileView(model: MainViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .cleanClickable { model.isProfileSettingVisible.value = true }
            .size(122.dp)
    ) {
        Spacer(
            modifier = Modifier
                .align(Alignment.Center)
                .border(width = 1.90164.dp, color = Color(0xFFFFCF31), shape = CircleShape)
                .padding(1.90164.dp)
                .width(116.dp)
                .height(116.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.main_setting_profile),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.main_setting_edit),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(38.dp)
                .height(38.dp)
        )
    }
}

@Composable
private fun NameText(text: String) {
    Text(
        text = text,
        fontFamily = MainFont.Pretendard,
        fontSize = 18.textDp,
        lineHeight = 25.textDp,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale20WH,
    )
}

@Composable
private fun InfoText(text: String) {
    Text(
        text = text,
        fontSize = 18.textDp,
        fontFamily = MainFont.Pretendard,
        lineHeight = 25.textDp,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale20WH,
    )
}

@Preview
@Composable
private fun ItemBox() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .clipToBounds()
            .padding(horizontal = 20.dp)
            .border(
                width = 0.5.dp,
                color = MainColor.OutlineBorder,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .background(color = MainColor.Greyscale02BK, shape = RoundedCornerShape(size = 16.dp))
    ) {
        SettingItem(R.drawable.main_setting_ask, "문의하기") {
            MainNavigator.startAskWebView(context)
        }
        SettingItem(R.drawable.main_seeing_rule, "이용약관") {
            MainNavigator.startRuleWebView(context)
        }
        SettingItem(R.drawable.main_setting_privacy, "개인정보처리방침") {
            MainNavigator.startPrivacyWebView(context)
        }
    }
}

@Preview
@Composable
private fun SettingItem(
    iconRes: Int = R.drawable.main_button_back,
    text: String = "문의하기",
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .rippleClickable { onClick.invoke() }
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
        )
        Text(
            text = text,
            fontSize = 16.textDp,
            fontFamily = MainFont.Pretendard,
            lineHeight = 22.textDp,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale19WH,
            modifier = Modifier.padding(start = 9.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.main_button_right_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 20.dp)
                .size(24.dp)
        )
    }
}