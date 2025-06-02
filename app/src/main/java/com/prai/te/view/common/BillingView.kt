package com.prai.te.view.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.VerticalGap
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainEvent
import com.prai.te.model.MainPremiumPlan
import com.prai.te.view.model.MainViewModel

@Preview
@Composable
internal fun BillingView(model: MainViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val message = model.billingMessage.collectAsStateWithLifecycle()
    val selected = model.selectedPlan.collectAsStateWithLifecycle()

    BackHandler {
        model.isBillingVisible.value = false
    }

    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
            .background(color = Color(0xFF000000))
            .verticalScroll(scrollState)
    ) {
        Header()
        ChooseView(
            plan = selected.value,
            onSelected = { model.selectedPlan.value = it }
        )
        Spacer(modifier = Modifier.height(120.dp))
        SecondBox()
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalView()
        Spacer(modifier = Modifier.height(60.dp))
        BottomBanner()
        Spacer(modifier = Modifier.height(20.dp))
        BottomButtonView(selected.value)
        Spacer(modifier = Modifier.height(10.dp))
    }

    val m = message.value
    if (m != null) {
        BillingMessageView(m.title, m.description)
    }
}

@Composable
private fun Header(model: MainViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.billing_close_icon),
            contentDescription = null,
            modifier = Modifier
                .cleanClickable { model.isBillingVisible.value = false }
                .padding(end = 18.dp)
                .size(24.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun ChooseView(
    plan: MainPremiumPlan,
    model: MainViewModel = viewModel(),
    onSelected: (MainPremiumPlan) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = buildAnnotatedString {
                append("하루 ")
                withStyle(style = SpanStyle(color = Color(0xFFFFCF31))) {
                    append("3분")
                }
                append("으로 부족했다면?\n")
                withStyle(style = SpanStyle(color = Color(0xFFFFCF31))) {
                    append("PRAI")
                }
                append("와 언제든 통화해보세요!")
            },
            fontSize = 22.textDp,
            lineHeight = 30.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(500),
            color = MainColor.OnSurfaceWH,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(R.drawable.billing_icon_1),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 103.dp)
                .align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(R.drawable.billing_ellipse_1),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 88.dp)
                .width(427.dp)
                .height(409.dp)
                .align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(R.drawable.billing_ellipse_2),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 65.dp)
                .width(283.dp)
                .height(289.dp)
                .align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(R.drawable.billing_ellipse_3),
            contentDescription = null,
            modifier = Modifier
                .width(421.dp)
                .height(430.dp)
                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 20.dp, end = 20.dp, top = 350.dp)
                .widthIn(max = 350.dp)
                .fillMaxWidth()
        ) {
            Year(plan == MainPremiumPlan.YEAR) {
                model.selectedPlan.value = MainPremiumPlan.YEAR
            }
            Box(
                modifier = Modifier
                    .padding(end = 18.dp)
                    .align(Alignment.TopEnd)
            ) {
                Best()
            }

        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 20.dp, end = 20.dp, top = 520.dp)
                .widthIn(max = 350.dp)
                .fillMaxWidth()
        ) {
            Month(plan == MainPremiumPlan.MONTH) {
                model.selectedPlan.value = MainPremiumPlan.MONTH
            }
        }
    }
}

@Composable
private fun Best() {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFFCF31), Color(0xFFEB7B1B)),
        start = Offset(0f, 0f),
        end = Offset(180f, 0f)
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(55.dp)
            .height(28.dp)
            .background(brush = gradient, shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = "Best",
            fontSize = 14.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(600),
            color = MainColor.Greyscale01BK,
        )
    }
}

@Composable
private fun Year(isSelected: Boolean, onSelected: () -> Unit) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) MainColor.PrimaryYE else Color.Transparent,
        animationSpec = tween(durationMillis = 10)
    )

    Box(
        modifier = Modifier
            .cleanClickable { onSelected.invoke() }
            .padding(top = 14.dp)
            .fillMaxWidth()
            .height(154.dp)
            .border(
                width = 1.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .background(color = Color(0xFF121212), shape = RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(top = 20.dp, start = 16.dp)) {
            Text(
                text = "연간 구독",
                fontSize = 14.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.Greyscale16WH,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(
                    text = "99,000 원 / 연간",
                    fontSize = 20.textDp,
                    lineHeight = 20.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(600),
                    color = MainColor.OnSurfaceWH,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "320,000 원",
                    fontSize = 16.textDp,
                    lineHeight = 16.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(400),
                    color = MainColor.Greyscale11WH,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
            Text(
                text = "월 8,250원의 특별한 혜택",
                fontSize = 14.textDp,
                lineHeight = 19.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.PrimaryYE,
            )
        }
        val gradient = Brush.linearGradient(
            colors = listOf(Color(0xFFFFCF31), Color(0xFFEB7B1B)),
            start = Offset(0f, 0f),
            end = Offset(600f, 0f)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = gradient,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
        ) {

            Image(
                painter = painterResource(R.drawable.billing_check),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(18.dp)
            )
            Text(
                text = "최대 69% 할인이 적용되었어요!",
                fontSize = 14.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = MainColor.Greyscale01BK,
            )
        }
    }
}

@Composable
private fun Month(isSelected: Boolean, onSelected: () -> Unit) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) MainColor.PrimaryYE else Color.Transparent,
        animationSpec = tween(durationMillis = 10)
    )

    Box(
        modifier = Modifier
            .cleanClickable { onSelected.invoke() }
            .padding(top = 16.dp)
            .fillMaxWidth()
            .height(88.dp)
            .border(
                width = 1.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .background(color = Color(0xFF121212), shape = RoundedCornerShape(16.dp))
    ) {

        Column(modifier = Modifier.padding(top = 20.dp, start = 16.dp)) {
            Text(
                text = "월간 구독",
                fontSize = 14.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.Greyscale16WH,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(
                    text = "19,000 원 / 개월",
                    fontSize = 20.textDp,
                    lineHeight = 20.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(600),
                    color = MainColor.OnSurfaceWH,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "27,000 원",
                    fontSize = 16.textDp,
                    lineHeight = 16.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(400),
                    color = MainColor.Greyscale11WH,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SecondBox() {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFFCF31), Color(0xFFEB7B1B)),
        start = Offset(0f, 0f),
        end = Offset(200f, 0f)
    )
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(start = 250.dp, top = 68.dp)
                .align(Alignment.TopCenter)
        ) {

            Image(
                painter = painterResource(R.drawable.billing_ellipse_1),
                contentDescription = null,
                modifier = Modifier
                    .width(427.dp)
                    .height(409.dp)
                    .align(Alignment.TopCenter)
            )
//            CircularGradientBox()
        }

        Text(
            text = buildAnnotatedString {
                append("매일 3분이 습관이 되면,\n영어가 달라져요.\n")
                withStyle(style = SpanStyle(brush = gradient)) {
                    append("영어루틴")
                }
                append("을 만들어 보세요.")
            },
            fontSize = 22.textDp,
            lineHeight = 30.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(500),
            color = MainColor.OnSurfaceWH,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(R.drawable.billing_clock_image),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 101.dp)
                .width(303.dp)
                .height(282.dp)
                .align(Alignment.TopCenter)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 468.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Image(
                    painter = painterResource(R.drawable.billing_prai_text),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .width(74.dp)
                        .height(28.dp)
                )
                Text(
                    text = " 가 영어 말하기를",
                    fontSize = 20.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(500),
                    color = MainColor.OnSurfaceWH,
                )
            }
            Text(
                text = "바꾸는 방법",
                fontSize = 20.textDp,
                lineHeight = 28.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(500),
                color = MainColor.OnSurfaceWH,
                textAlign = TextAlign.Center,
            )

        }
    }
}

@Preview
@Composable
private fun HorizontalView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.img_list1),
            contentDescription = null,
            modifier = Modifier
                .width(256.dp)
                .height(335.dp)
        )
        Image(
            painter = painterResource(R.drawable.img_list2),
            contentDescription = null,
            modifier = Modifier
                .width(256.dp)
                .height(335.dp)
        )

        Image(
            painter = painterResource(R.drawable.img_list3),
            contentDescription = null,
            modifier = Modifier
                .width(256.dp)
                .height(335.dp)
        )
    }
}

@Composable
private fun BottomBanner() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.billing_bottom_banner),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun BottomButtonView(selectedPlan: MainPremiumPlan, model: MainViewModel = viewModel()) {
    val text = when (selectedPlan) {
        MainPremiumPlan.YEAR -> "연간 구독 시작하기"
        MainPremiumPlan.MONTH -> "월간 구독 시작하기"
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(

        )
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0x80FFCF31),
                    ambientColor = Color(0x80FFCF31)
                )
                .width(335.dp)
                .cleanClickable { model.dispatchEvent(MainEvent.BillingItemClick(selectedPlan)) }
                .height(50.dp)
                .background(color = MainColor.PrimaryYE, shape = RoundedCornerShape(size = 60.dp))
        ) {
            Text(
                text = text,
                fontSize = 16.textDp,
                lineHeight = 22.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = MainColor.Greyscale01BK,
                modifier = Modifier.align(Alignment.Center)
            )
            Image(
                painter = painterResource(R.drawable.billing_right_arrow),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 18.dp)
                    .align(Alignment.CenterEnd)
            )
        }
        Text(
            text = "구매 복원하기",
            fontSize = 14.textDp,
            lineHeight = 14.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale17WH,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .cleanClickable {
                    model.dispatchEvent(MainEvent.BillingRecoverClick)
                }
        )
    }
}

@Preview
@Composable
private fun BillingMessageView(
    title: String = "결제가 진행 중이에요 :)",
    description: String = "잠시만 기다려 주세요.\n앱을 종료하면 결제가 중단될 수 있어요."
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xCC000000))
            .cleanClickable {}
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .background(
                    color = MainColor.Greyscale03BK,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.textDp,
                lineHeight = 20.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = MainColor.PrimaryYE,
                textAlign = TextAlign.Center,
            )
            VerticalGap(10)
            Text(
                text = description,
                fontSize = 14.textDp,
                lineHeight = 19.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.Greyscale19WH,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CircularGradientBox() {
    Box(
        modifier = Modifier
            .size(440.dp)
            .alpha(0.16f)
            .drawWithCache {
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)
                val gradient = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFCF31), Color(0x00FFCF31)),
                    center = center,
                    radius = radius
                )
                onDrawBehind {
                    drawRect(brush = gradient)
                }
            }
    )
}