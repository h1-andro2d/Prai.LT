package com.prai.te.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.view.model.MainRepositoryViewModel
import kotlinx.coroutines.delay

@Composable
internal fun AgeInputEditText(
    ageText: String,
    autoFocus: Boolean = false,
    repository: MainRepositoryViewModel = viewModel()
) {
    val controller = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember(ageText) {
        mutableStateOf(
            TextFieldValue(
                text = ageText,
                selection = TextRange(ageText.length)
            )
        )
    }
    LaunchedEffect(Unit) {
        if (autoFocus) {
            delay(100L)
            focusRequester.requestFocus()
            controller?.show()
        }
    }

    Column {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val newText = newValue.text
                if (newText.length <= 2 && newText.all { it.isDigit() }) {
                    repository.ageText.value = newText
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { controller?.hide() }
            ),
            cursorBrush = SolidColor(MainColor.Greyscale18WH),
            modifier = Modifier
                .focusRequester(focusRequester)
                .height(0.dp)
        )
        Row {
            NumberBox(
                digit = ageText.getOrNull(0),
                onClick = {
                    focusRequester.requestFocus()
                    controller?.show()
                }
            )

            Spacer(modifier = Modifier.width(6.dp))

            NumberBox(
                digit = ageText.getOrNull(1),
                onClick = {
                    focusRequester.requestFocus()
                    controller?.show()
                }
            )
        }
    }
}

@Composable
private fun NumberBox(digit: Char?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(47.dp)
            .height(59.dp)
            .border(
                width = 1.dp,
                color = MainColor.OutlineBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = MainColor.Greyscale02BK,
                shape = RoundedCornerShape(10.dp)
            )
            .cleanClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (digit != null) {
            Text(
                text = digit.toString(),
                fontSize = 26.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.OnSurfaceWH,
            )
        }
    }
}