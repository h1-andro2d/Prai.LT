package com.prai.te.view.premium

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.HorizontalGap
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.MainLogger
import com.prai.te.common.VerticalGap
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainOutCase
import com.prai.te.view.common.CommonBackAndTitleHeader
import com.prai.te.view.model.MainViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
internal fun MyMembershipScreen(model: MainViewModel = viewModel()) {
    BackHandler {
        model.isMyMembershipVisible.value = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .cleanClickable {}
            .background(color = Color(0xFF000000))
    ) {
        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            CommonBackAndTitleHeader("나의 구독 관리", { model.isMyMembershipVisible.value = false })
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 60.dp)
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .background(color = Color(0xFF000000))
        ) {
            VerticalGap(20)
            Description()
            VerticalGap(12)
            MyPlanView()
            VerticalGap(40)
            ButtonBox()
            VerticalGap(40)
            DescriptionText()
            VerticalGap(50)
        }
    }
}

@Preview
@Composable
private fun Description() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "현재 이용 중",
            fontSize = 14.textDp,
            lineHeight = 19.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale18WH,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Preview
@Composable
private fun MyPlanView() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MainColor.OutlineBorder,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .background(
                color = MainColor.Greyscale02BK,
                shape = RoundedCornerShape(size = 16.dp)
            )
    ) {
        VerticalGap(20)
        Title()
        VerticalGap(12)
        BenefitItem("무제한 통화")
        VerticalGap(8)
        BenefitItem("AI 음성 선택 & 모드 설정")
        VerticalGap(8)
        BenefitItem("상황별 시나리오 대화")
        VerticalGap(8)
        BenefitItem("발음·문장 피드백 기능 (추가 예정)")
        VerticalGap(21)
        Divider()
        PaymentSchedule()
    }
}

@Composable
private fun Title() {
    Text(
        text = "연간 구독",
        fontSize = 16.textDp,
        lineHeight = 22.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(600),
        color = MainColor.Greyscale20WH,
        modifier = Modifier.padding(start = 20.dp)
    )
}

@Composable
private fun BenefitItem(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.common_check_enabled),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp, end = 6.dp)
                .size(16.dp)
        )
        Text(
            text = title,
            fontSize = 14.textDp,
            lineHeight = 19.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale19WH,
        )
    }
}

@Composable
private fun PriceText() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "99,000원",
            fontSize = 16.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(600),
            color = MainColor.Greyscale20WH,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .background(color = MainColor.Greyscale06BK)
            .fillMaxWidth()
            .height(1.dp)
    )
}

@Composable
private fun PaymentSchedule(model: MainViewModel = viewModel()) {
    val expireTime = model.premiumExpiresTime.collectAsStateWithLifecycle()
    val timeValue = convertIsoToYyyyMmDd(expireTime.value)
    val text = if (timeValue == null) {
        "갱신 정보는 PlayStore에서 확인 가능합니다"
    } else {
        "$timeValue 갱신 예정"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.textDp,
            lineHeight = 19.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(600),
            color = MainColor.Greyscale20WH,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterStart)
        )
    }
}

fun convertIsoToYyyyMmDd(isoDate: String?): String? {
    if (isoDate == null) {
        return null
    }
    try {
        return isoDate.split("T")[0].replace("-", ".")
    } catch (exception: Exception) {
        MainLogger.ETC.log(exception, "convertIsoToYyyyMmDd, $exception")
        return null
    }
}

@Composable
private fun ButtonBox(model: MainViewModel = viewModel()) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "환불하기",
            fontSize = 16.textDp,
            lineHeight = 24.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale13WH,
            modifier = Modifier.cleanClickable {
                model.outCase.value = MainOutCase.SUBSCRIPTION_REFUND
            }
        )
        HorizontalGap(40)
        Text(
            text = "구독 취소",
            fontSize = 16.textDp,
            lineHeight = 24.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale13WH,
            modifier = Modifier.cleanClickable {
                model.outCase.value = MainOutCase.SUBSCRIPTION_CANCEL
            }
        )
    }
}

@Composable
private fun DescriptionText() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "멤버십 유의사항",
            fontSize = 14.textDp,
            lineHeight = 14.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(600),
            color = MainColor.Greyscale12WH,
        )
        VerticalGap(16)

        val bulletPoints = listOf(
            "결제 금액은 부가가치세(VAT)가 포함된 가격입니다.",
            "등록하신 결제 수단으로 정기 결제일에 멤버십 이용 금액이 자동 결제됩니다.",
            "환불 및 구독 취소는 각 스토어의 정책을 따릅니다.",
            "미성년자의 결제는 원칙적으로 법정대리인의 명의 또는 동의를 받아야 하며, 동의 없이 체결된 결제는 법정대리인이 취소할 수 있습니다.",
            "기타 문의 사항은 '문의하기'를 통해 전달해 주세요."
        )

        val annotatedString = buildAnnotatedString {
            bulletPoints.forEachIndexed { index, text ->
                val paragraphStyle =
                    ParagraphStyle(
                        textIndent = TextIndent(
                            firstLine = 0.textDp,
                            restLine = 8.textDp
                        )
                    )
                withStyle(paragraphStyle) {
                    append("- $text")
                    if (index < bulletPoints.size - 1) {
                    }
                }
            }
        }

        Text(
            text = annotatedString,
            fontSize = 12.textDp,
            lineHeight = 18.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale12WH,
        )
    }
}