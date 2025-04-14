package com.prai.te.view.legacy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.clearFocusRippleClickable
import com.prai.te.common.textDp

@Composable
private fun BorderText(text: String = "NAMENAME", onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 16.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale19WH,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .border(
                width = 1.dp,
                color = MainColor.OutlineBorder,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .fillMaxWidth()
            .background(color = MainColor.Greyscale02BK, shape = RoundedCornerShape(size = 10.dp))
            .clipToBounds()
            .clearFocusRippleClickable() { onClick.invoke() }
            .padding(horizontal = 16.dp, vertical = 20.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialTime: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialTime
    )

    val datePickerColors = DatePickerDefaults.colors(
        containerColor = MainColor.Greyscale02BK,
        titleContentColor = MainColor.White,
        headlineContentColor = MainColor.Greyscale20WH,
        weekdayContentColor = MainColor.Greyscale20WH,
        subheadContentColor = MainColor.Greyscale20WH,
        navigationContentColor = MainColor.Greyscale20WH,
        yearContentColor = MainColor.White,
        disabledYearContentColor = MainColor.Greyscale02BK,
        currentYearContentColor = MainColor.PrimaryYE,
        selectedYearContentColor = MainColor.Greyscale02BK,
        disabledSelectedYearContentColor = MainColor.Greyscale02BK,
        selectedYearContainerColor = MainColor.PrimaryYE,
        disabledSelectedYearContainerColor = MainColor.Greyscale02BK,
        dayContentColor = MainColor.Greyscale20WH,
        disabledDayContentColor = MainColor.Greyscale02BK,
        selectedDayContentColor = MainColor.Greyscale02BK,
        disabledSelectedDayContentColor = MainColor.Greyscale02BK,
        selectedDayContainerColor = MainColor.PrimaryYE,
        disabledSelectedDayContainerColor = MainColor.Red,
        todayContentColor = MainColor.Greyscale20WH,
        todayDateBorderColor = MainColor.Transparent,
        dayInSelectionRangeContentColor = MainColor.Transparent,
        dayInSelectionRangeContainerColor = MainColor.Transparent,
        dividerColor = MainColor.OutlineBorder
    )

    DatePickerDialog(
        colors = datePickerColors,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(
                    "선택",
                    fontFamily = MainFont.Pretendard,
                    color = MainColor.PrimaryYE
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "취소",
                    fontFamily = MainFont.Pretendard, color = MainColor.Greyscale19WH
                )
            }
        }
    ) {
        DatePicker(
            colors = datePickerColors,
            state = datePickerState,
            showModeToggle = false
        )
    }
}