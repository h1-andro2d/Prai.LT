package com.prai.te.view.setting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.VerticalGap
import com.prai.te.common.cleanClickable
import com.prai.te.common.clearFocusCleanClickable
import com.prai.te.common.clearFocusRippleClickable
import com.prai.te.common.textDp
import com.prai.te.view.common.AgeInputEditText
import com.prai.te.view.common.GenderSelection
import com.prai.te.view.common.NameInputEditText
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.model.UserGender

@Composable
internal fun MainProfileSettingView(
    nameText: String,
    ageText: String,
    gender: UserGender?,
    model: MainViewModel = viewModel(),
    repository: MainRepositoryViewModel = viewModel()
) {
    BackHandler {
        model.isProfileSettingVisible.value = false
        repository.rollbackProfileSetting()
    }

    LaunchedEffect(Unit) {
        repository.makeProfileSettingCache()
    }

    val buttonActive by remember(nameText, ageText) {
        derivedStateOf { nameText != "" && ageText.length == 2 }
    }

    Box(
        modifier = Modifier
            .clearFocusCleanClickable()
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
                    .clearFocusCleanClickable(lazy = false) {
                        model.isProfileSettingVisible.value = false
                        repository.rollbackProfileSetting()
                    }
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(24.dp)
            )
            Text(
                text = "프로필 설정",
                fontSize = 18.textDp,
                fontFamily = MainFont.Pretendard,
                lineHeight = 25.textDp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        LazyColumn(modifier = Modifier.padding(top = 60.dp, bottom = 65.dp)) {
            item { VerticalGap(20) }
            item { CategoryText("이름") }
            item { VerticalGap(16) }
            item { NameInputEditText() }
            item { VerticalGap(40) }
            item { CategoryText("성별") }
            item { VerticalGap(16) }
            item { GenderSelection(gender) }
            item { VerticalGap(40) }
            item { AgeTextBox() }
            item { VerticalGap(60) }
            item { LogOutRow() }
        }
        CustomSaveButton(
            isActive = buttonActive,
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {
                if (buttonActive) {
                    model.isProfileSettingVisible.value = false
                    repository.saveProfileSetting()
                }
            }
        )
    }
}

@Composable
internal fun CustomSaveButton(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFFFFCF31) else Color(0xFFFFFFFF),
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColor"
    )

    Box(modifier = modifier) {
        Text(
            text = "저장",
            textAlign = TextAlign.Center,
            fontSize = 18.textDp,
            fontFamily = MainFont.Pretendard,
            color = Color(0xFF121212),
            fontWeight = FontWeight.W600,
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 20.dp)
                .fillMaxWidth()
                .height(53.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(60.dp)
                )
                .clearFocusRippleClickable {
                    onClick.invoke()
                }
                .padding(vertical = 14.dp)
        )
    }
}

@Composable
private fun CategoryText(text: String = "이름") {
    Text(
        text = text,
        fontFamily = MainFont.Pretendard,
        fontSize = 16.textDp,
        lineHeight = 24.textDp,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale18WH,
        modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun AgeTextBox() {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            text = "출생연도",
            fontSize = 16.textDp,
            lineHeight = 24.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale18WH,
        )
        Spacer(modifier = Modifier.weight(1f))
        AgeInputEditText()
        Text(
            text = "년",
            fontSize = 16.textDp,
            lineHeight = 20.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(500),
            color = MainColor.Greyscale19WH,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun LogOutRow(model: MainViewModel = viewModel()) {
    val controller = LocalSoftwareKeyboardController.current

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "로그아웃",
            fontSize = 16.textDp,
            lineHeight = 24.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale13WH,
            modifier = Modifier.cleanClickable {
                controller?.hide()
                model.isLogoutDialog.value = true
            }
        )
        Spacer(modifier = Modifier.width(40.dp))
        Text(
            text = "회원탈퇴",
            fontSize = 16.textDp,
            lineHeight = 24.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale13WH,
            modifier = Modifier.cleanClickable {
                controller?.hide()
                model.isDeleteUserDialog.value = true
            }
        )
    }
}