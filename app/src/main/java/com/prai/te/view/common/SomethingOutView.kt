package com.prai.te.view.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.VerticalGap
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainEvent
import com.prai.te.model.MainOneButtonDialogData
import com.prai.te.model.MainOutCase
import com.prai.te.view.common.dialog.OneButtonDialog
import com.prai.te.view.model.MainViewModel

@Preview
@Composable
internal fun SomethingOutView(
    case: MainOutCase = MainOutCase.SUBSCRIPTION_CANCEL,
    model: MainViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    var selected = remember { mutableStateOf(setOf<String>()) }
    var etcText by remember { mutableStateOf("") }

    BackHandler {
        model.outCase.value = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .cleanClickable {}
            .background(color = Color(0xFF000000))
    ) {
        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            CommonBackAndTitleHeader(case.title, { model.outCase.value = null })
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 60.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxSize()
                .background(color = Color(0xFF000000))
                .verticalScroll(scrollState)
        ) {
            VerticalGap(20)
            OutIcon()
            VerticalGap(17)
            Description1(case.description)
            VerticalGap(17)
            Description2()
            VerticalGap(30)
            Items(selected.value, case.items) { text ->
                if (text in selected.value) {
                    selected.value = selected.value - setOf(text)
                } else {
                    selected.value = selected.value + setOf(text)
                }
            }
            VerticalGap(20)
            EtcInputDescription()
            VerticalGap(6)
            EtcInputEditText { etcText = it }
            VerticalGap(60)
            CommonConfirmButton(
                case.buttonText,
                enabled = selected.value.isNotEmpty() || etcText.isNotEmpty()
            ) {
                handleAction(case, model, selected.value, etcText)
            }
            VerticalGap(10)
        }
    }
}

private fun handleAction(
    case: MainOutCase,
    model: MainViewModel,
    selected: Set<String>,
    text: String
) {
    when (case) {
        MainOutCase.DELETE_USER -> {
            model.isDeleteUserDialog.value = true
        }

        MainOutCase.SUBSCRIPTION_CANCEL -> {
            model.oneButtonDialogData.value = MainOneButtonDialogData.SUBSCRIPTION_CANCEL
        }

        MainOutCase.SUBSCRIPTION_REFUND -> {
            model.oneButtonDialogData.value = MainOneButtonDialogData.SUBSCRIPTION_REFUND
        }
    }
    val message = "case: ${case.title}, selected: $selected, etc: $text"
    model.dispatchEvent(MainEvent.SomethingOutClick(message))
    model.outCase.value = null
    model.clearView()
}

@Composable
private fun OutIcon() {
    Image(
        painter = painterResource(id = R.drawable.out_icon),
        contentDescription = null,
        modifier = Modifier
            .width(121.dp)
            .height(100.dp)
    )
}

@Composable
private fun Description1(text: String) {
    Text(
        text = text,
        fontSize = 18.textDp,
        lineHeight = 23.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(600),
        color = Color(0xFFFFFFFF),
    )
}

@Composable
private fun Description2() {
    Text(
        text = "아쉬웠던 점이 있다면 꼭 알려주세요.\n다음엔 더 좋은 친구가 되어드릴게요.",
        fontSize = 16.textDp,
        lineHeight = 22.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale19WH,
    )
}

@Composable
private fun Items(selected: Set<String>, items: List<String>, onSelect: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        items.forEach {
            Item(it in selected, it, onSelect = onSelect)
        }
    }
}

@Preview
@Composable
private fun Item(
    isSelected: Boolean = true, text: String = "test", onSelect: (String) -> Unit = {}
) {
    val borderColor = if (isSelected) {
        MainColor.PrimaryYE
    } else {
        Color.Transparent
    }

    val textColor = if (isSelected) {
        MainColor.PrimaryYE
    } else {
        MainColor.Greyscale17WH
    }

    val imageResource = if (isSelected) {
        R.drawable.common_check_enabled
    } else {
        R.drawable.common_check_disabled
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .border(
                width = 0.5.dp, color = borderColor, shape = RoundedCornerShape(size = 10.dp)
            )
            .background(color = MainColor.Greyscale02BK, shape = RoundedCornerShape(size = 10.dp))
            .clipToBounds()
            .clickable { onSelect.invoke(text) }) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 15.dp, end = 4.dp)
                .size(16.dp)
        )
        Text(
            text = text,
            fontSize = 16.textDp,
            lineHeight = 20.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = textColor,
            modifier = Modifier.padding(start = 3.dp)
        )
    }
}

@Preview
@Composable
private fun EtcInputDescription() {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "기타 입력",
            fontSize = 16.textDp,
            lineHeight = 20.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.Greyscale17WH
        )
    }
}

@Composable
private fun EtcInputEditText(onTextChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        CommonTextField(200, "불편했던 점이 있다면 자유롭게 알려주세요.", onTextChange)
    }
}