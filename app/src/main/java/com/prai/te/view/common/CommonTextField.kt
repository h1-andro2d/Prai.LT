package com.prai.te.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.textDp

@Preview
@Composable
internal fun CommonTextField(
    limit: Int = 200,
    placeHolderText: String = "PLACEHOLDER",
    onTextChange: (String) -> Unit ={}
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val controller = LocalSoftwareKeyboardController.current

    TextField(
        value = textFieldValue,
        onValueChange = { inner ->
            if (inner.text.length <= limit) {
                textFieldValue = inner
                onTextChange.invoke(inner.text)
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
            onDone = { controller?.hide() }
        ),
        placeholder = {
            Text(
                text = placeHolderText,
                fontSize = 16.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(400),
                color = MainColor.Greyscale12WH
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MainColor.Greyscale02BK,
                shape = RoundedCornerShape(size = 10.dp)
            )
    )
}