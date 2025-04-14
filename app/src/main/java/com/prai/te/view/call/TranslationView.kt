package com.prai.te.view.call

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainTranslationState
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.shimmerLoadingAnimation

@Preview
@Composable
internal fun TranslationOverlayView(model: MainViewModel = viewModel()) {
    val state = model.translationState.collectAsStateWithLifecycle()
    val original = when (val current = state.value) {
        is MainTranslationState.Requested -> current.originalText
        is MainTranslationState.Done -> current.originalText
        is MainTranslationState.None -> "There is no available text now."
    }
    BackHandler {
        model.isTranslationOverlayVisible.value = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .widthIn(max = 450.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .background(
                        color = Color(0xFF222222),
                        shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.main_icon_translation),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = "번역하기",
                        fontSize = 18.textDp,
                        fontFamily = MainFont.Pretendard,
                        lineHeight = 25.textDp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFFFFFFF)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.main_setting_x),
                        contentDescription = null,
                        modifier = Modifier
                            .cleanClickable { model.isTranslationOverlayVisible.value = false }
                            .size(30.dp)
                            .padding(end = 6.dp)
                    )
                }
                Text(
                    text = original,
                    fontSize = 17.textDp,
                    fontFamily = MainFont.Pretendard,
                    lineHeight = 23.textDp,
                    fontWeight = FontWeight(300),
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.padding(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .animateContentSize()
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFCF31),
                        shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                    )
                    .padding(20.dp)
            ) {
                val currentState = state.value
                if (currentState is MainTranslationState.Done) {
                    Text(
                        text = currentState.translatedText,
                        fontSize = 16.textDp,
                        fontFamily = MainFont.Pretendard,
                        lineHeight = 22.textDp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000)
                    )
                } else {
                    SkeletonBox()
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 14.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.main_icon_egg),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 6.dp)
                )
                Text(
                    text = "이 문장을 말로 따라하면 더 오래 기억돼요!",
                    fontSize = 14.textDp,
                    lineHeight = 19.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFCF31),
                )
            }
        }
    }
}

@Preview
@Composable
private fun SkeletonBox() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Spacer(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(size = 60.dp))
                .width(204.dp)
                .height(24.dp)
                .background(color = Color(0xFFD9AB16), shape = RoundedCornerShape(size = 60.dp))
                .shimmerLoadingAnimation(false)
        )
        Spacer(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(size = 60.dp))
                .width(277.dp)
                .height(24.dp)
                .background(color = Color(0xFFD9AB16), shape = RoundedCornerShape(size = 60.dp))
                .shimmerLoadingAnimation(false)
        )
        Spacer(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(size = 60.dp))
                .width(142.dp)
                .height(24.dp)
                .background(color = Color(0xFFD9AB16), shape = RoundedCornerShape(size = 60.dp))
                .shimmerLoadingAnimation(false)
        )
    }
}