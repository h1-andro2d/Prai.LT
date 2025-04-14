package com.prai.te.view.intro

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainEvent
import com.prai.te.model.MainIntroState
import com.prai.te.view.common.AgeInputEditText
import com.prai.te.view.common.GenderSelection
import com.prai.te.view.common.NameInputEditText
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.model.UserGender

@Composable
internal fun OnboardingView(
    model: MainViewModel = viewModel(),
    repository: MainRepositoryViewModel = viewModel()
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val controller = LocalSoftwareKeyboardController.current
    val name = repository.nameText.collectAsStateWithLifecycle()
    val age = repository.ageText.collectAsStateWithLifecycle()
    val gender = repository.selectedGender.collectAsStateWithLifecycle()

    val active = when (currentPage) {
        0 -> name.value != ""
        1 -> gender.value != null
        2 -> age.value.length == 2
        else -> false
    }

    LaunchedEffect(Unit) { currentPage = 0 }

    BackHandler {
        if (currentPage == 0) {
            model.introState.value = MainIntroState.LOGIN
            model.dispatchEvent(MainEvent.LogoutRequest) // TODO: Remove this
        } else {
            currentPage--
        }
    }

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        ProgressIndicator(currentPage)

        AnimatedVisibility(
            visible = currentPage == 0,
            enter = fadeIn(animationSpec = tween(700)) + slideInHorizontally { x -> x },
            exit = fadeOut(animationSpec = tween(700)) + slideOutHorizontally { x -> -x },
            modifier = Modifier
                .padding(top = 87.dp)
                .fillMaxSize()
        ) {
            View1(name.value)
        }
        AnimatedVisibility(
            visible = currentPage == 1,
            enter = fadeIn(animationSpec = tween(700)) + slideInHorizontally { x -> x },
            exit = fadeOut(animationSpec = tween(700)) + slideOutHorizontally { x -> -x },
            modifier = Modifier
                .padding(top = 87.dp)
                .fillMaxSize()
        ) {
            View2(gender.value)
        }
        AnimatedVisibility(
            visible = currentPage == 2,
            enter = fadeIn(animationSpec = tween(700)) + slideInHorizontally { x -> x },
            exit = fadeOut(animationSpec = tween(700)) + slideOutHorizontally { x -> -x },
            modifier = Modifier
                .padding(top = 87.dp)
                .fillMaxSize()
        ) {
            View3(age.value)
        }

        Crossfade(
            targetState = active,
            label = "conversation_list_cross_fade",
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.ime)
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter)
        ) { activeButton ->
            if (activeButton) {
                NextButtonActive(
                    onClick = {
                        if (active) {
                            if (currentPage == 0 || currentPage == 2) {
                                controller?.hide()
                            }
                            if (currentPage < 2) {
                                currentPage++
                            } else {
                                model.dispatchEvent(MainEvent.RegisterUserRequest)
//                                model.introState.value = MainIntroState.DONE
                            }
                        }
                    }
                )
            } else {
                NextButtonInactive()
            }
        }
    }
}

@Composable
private fun ProgressIndicator(currentPage: Int) {
    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(3) { index ->
            val isActive = currentPage >= index

            val color by animateColorAsState(
                targetValue = if (isActive) MainColor.PrimaryYE else MainColor.Greyscale02BK,
                animationSpec = tween(durationMillis = 300),
                label = "OnboardingView_ProgressBarColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color = color)
            )
            if (index < 2) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun NextButtonActive(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Image(
        painter = painterResource(R.drawable.main_icon_next_active),
        contentDescription = null,
        modifier = modifier
            .size(58.dp)
            .cleanClickable(onClick)
    )
}

@Composable
private fun NextButtonInactive(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.main_icon_next_inactive),
        contentDescription = null,
        modifier = modifier
            .size(58.dp)
    )
}

@Composable
private fun View1(nameText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_icon_1),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(26.dp)
        )
        Text(
            text = "PRAI가 대화할 때 \n어떻게 불러드릴까요?",
            fontSize = 20.textDp,
            lineHeight = 26.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 26.dp)
        )
        NameInputEditText(nameText, autoFocus = true)
    }
}

@Composable
private fun View2(gender: UserGender?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_icon_2),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(26.dp)
        )
        Text(
            text = "더 자연스럽게 말 걸기 위해,\n성별을 선택해 주세요!",
            fontSize = 20.textDp,
            lineHeight = 26.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 26.dp)
        )
        GenderSelection(gender)
    }
}

@Composable
private fun View3(ageText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_icon_3),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(26.dp)
        )
        Text(
            text = "연령대에 맞는 기능들을 준비할게요.\n태어난 해를 알려주세요!",
            fontSize = 20.textDp,
            lineHeight = 26.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 26.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            AgeInputEditText(ageText, autoFocus = true)
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
        Text(
            text = "출생연도의 뒷자리 두 숫자만 입력해 주세요. ex) 99, 04",
            fontSize = 14.textDp,
            lineHeight = 19.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale13WH,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 25.dp)
        )
    }
}
