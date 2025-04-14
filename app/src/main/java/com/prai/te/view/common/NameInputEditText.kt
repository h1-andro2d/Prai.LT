package com.prai.te.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import com.prai.te.common.textDp
import com.prai.te.view.model.MainRepositoryViewModel
import kotlinx.coroutines.delay

@Composable
internal fun NameInputEditText(
    autoFocus: Boolean = false,
    repository: MainRepositoryViewModel = viewModel()
) {
    val controller = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = repository.nameText.value,
                selection = TextRange(repository.nameText.value.length)
            )
        )
    }
    LaunchedEffect(Unit) {
        if (autoFocus) {
            delay(200L)
            focusRequester.requestFocus()
            controller?.show()
        }
    }

    TextField(
        value = textFieldValue,
        onValueChange = { inner ->
            if (inner.text.length <= 20) {
                textFieldValue = inner
                repository.nameText.value = inner.text
            }
        },
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = MainColor.OnSurfaceWH
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        colors = TextFieldDefaults.colors().copy(
            cursorColor = MainColor.Greyscale18WH,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                controller?.hide()
            }
        ),
        placeholder = {
            Text(
                text = "이름은 20자 이하로 적어주세요.",
                fontSize = 16.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.Greyscale12WH
            )
        },
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .border(
                width = 1.dp,
                color = MainColor.OutlineBorder,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .fillMaxWidth()
            .background(
                color = MainColor.Greyscale02BK,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .focusRequester(focusRequester)
    )
}