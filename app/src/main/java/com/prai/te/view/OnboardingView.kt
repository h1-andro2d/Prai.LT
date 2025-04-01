package com.prai.te.view

import android.widget.RadioButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun OnboardingView() {
    var text by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf("No date selected") }
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    var selectedGender by remember { mutableStateOf("남성") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("이름")
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text here") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            GenderRadioButton(
                text = "남성",
                selected = selectedGender == "남성",
                onClick = { selectedGender = "남성" }
            )

            Spacer(modifier = Modifier.width(16.dp))

            GenderRadioButton(
                text = "여성",
                selected = selectedGender == "여성",
                onClick = { selectedGender = "여성" }
            )
        }

        Text("선택된 성별: $selectedGender")

        Button(onClick = {
            showPicker = true
        }) {
            Text("달력열기")
        }

        if (showPicker) {
            DatePickerModal(
                onDateSelected = { dateMillis ->
                    selectedDateText = dateMillis?.let {
                            dateFormat.format(Date(it))
                        } ?: "No data selected"
                },
                onDismiss = {
                    showPicker = false
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("You entered: $selectedDateText")
    }
}

@Composable
fun GenderRadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val colors = DatePickerDefaults.colors(
        containerColor = Color.White,
        selectedDayContainerColor = Color.Blue,
        todayDateBorderColor = Color.Blue
    )

    DatePickerDialog(
        colors = colors,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(
                    "선택",
                    color = Color.Blue
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color.Blue)
            }
        }
    ) {
        DatePicker(
            colors = colors,
            state = datePickerState
        )
    }
}

@Preview
@Composable
private fun OnboardingPreview() {
    OnboardingView()
}