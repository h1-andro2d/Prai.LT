package com.prai.te.view.setting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainTimeUtil
import com.prai.te.common.VerticalGap
import com.prai.te.common.clearFocusCleanClickable
import com.prai.te.common.clearFocusRippleClickable
import com.prai.te.common.rippleClickable
import com.prai.te.view.common.MainSaveButton
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.model.UserGender

@Preview
@Composable
internal fun MainProfileSettingView(
    model: MainViewModel = viewModel(),
    repository: MainRepositoryViewModel = viewModel()
) {
    var showPicker by remember { mutableStateOf(false) }
    var selectedData = repository.selectedBirthDateMills.collectAsStateWithLifecycle()

    BackHandler {
        model.isProfileSettingVisible.value = false
        repository.rollbackProfileSetting()
    }

    LaunchedEffect(Unit) {
        repository.makeProfileSettingCache()
    }

    Box(
        modifier = Modifier
            .clearFocusCleanClickable()
            .fillMaxSize()
            .background(color = Color(0xFF000000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_button_back),
                contentDescription = null,
                modifier = Modifier
                    .clearFocusCleanClickable(lazy = false) {
                        model.isProfileSettingVisible.value = false
                        repository.rollbackProfileSetting()
                    }
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(24.dp)
            )
            Text(
                text = "프로필 설정",
                fontSize = 18.sp,
                lineHeight = 25.2.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        LazyColumn(modifier = Modifier.padding(top = 60.dp, bottom = 65.dp)) {
            item { VerticalGap(20) }
            item { CategoryText("이름") }
            item { VerticalGap(16) }
            item { BorderEditText() }
            item { VerticalGap(40) }
            item { CategoryText("성별") }
            item { VerticalGap(16) }
            item {
                GenderSelection()
            }
            item { VerticalGap(40) }
            item { CategoryText("생년월일") }
            item { VerticalGap(16) }
            item {
                BorderText(MainTimeUtil.brithMillsToString(selectedData.value)) {
                    showPicker = true
                }
            }
        }
        MainSaveButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clearFocusCleanClickable {
                    model.isProfileSettingVisible.value = false
                    repository.saveProfileSetting()
                }
        )
    }

    if (showPicker) {
        DatePickerModal(
            initialTime = selectedData.value,
            onDateSelected = { dateMillis ->
                repository.selectedBirthDateMills.value = dateMillis
            },
            onDismiss = {
                showPicker = false
            }
        )
    }
}

@Composable
private fun CategoryText(text: String = "이름") {
    Text(
        text = text,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(400),
        color = MainColor.Greyscale18WH,
        modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun BorderText(text: String = "NAMENAME", onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 16.sp,
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

@Preview
@Composable
private fun BorderEditText(repository: MainRepositoryViewModel = viewModel()) {
    val nameText = repository.nameText.collectAsStateWithLifecycle()
    val controller = LocalSoftwareKeyboardController.current

    TextField(
        value = nameText.value,
        onValueChange = { new ->
            if (new.length <= 20) {
                repository.nameText.value = new
            }
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
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        // 길게 누를 때 아무 동작도 하지 않음 (기본 동작 억제 시도)
                    }
                )
            },
        interactionSource = remember { MutableInteractionSource() },
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
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
        ) {
            // focusManager.clearFocus()
        },
        placeholder = {
            Text(
                text = "이름은 20자 이하로 적어주세요.", // Your hint text here
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                color = MainColor.OnSurfaceWH.copy(alpha = 0.5f) // Semi-transparent hint color
            )
        }
    )
}

@Composable
internal fun GenderSelection(repository: MainRepositoryViewModel = viewModel()) {
    val genders = listOf(UserGender.MALE, UserGender.FEMALE)
    val selected = repository.selectedGender.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.5.dp)
    ) {
        genders.forEach { gender ->
            val isSelected = gender == selected.value

            val animatedDescriptionColor by animateColorAsState(
                targetValue = if (isSelected) {
                    ProfileSettingColors.selectedTextColor
                } else {
                    ProfileSettingColors.defaultTextColor
                },
                label = "genderDescColor"
            )
            val animatedBorderColor by animateColorAsState(
                targetValue = if (isSelected) {
                    ProfileSettingColors.selectedBorderColor
                } else {
                    ProfileSettingColors.defaultBorderColor
                },
                label = "genderBorderColor"
            )
            val animatedBackgroundColor by animateColorAsState(
                targetValue = if (isSelected) {
                    ProfileSettingColors.selectedBgColor
                } else {
                    ProfileSettingColors.defaultBgColor
                },
                label = "genderBackgroundColor"
            )

            GenderItem(
                gender = gender,
                descriptionColor = animatedDescriptionColor,
                borderColor = animatedBorderColor,
                bgColor = animatedBackgroundColor,
                onClick = {
                    repository.selectedGender.value = it
                },
                modifier = Modifier.weight(1f)
            )


        }
    }
}

@Composable
private fun GenderItem(
    gender: UserGender,
    descriptionColor: Color,
    borderColor: Color,
    bgColor: Color,
    onClick: (UserGender) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clearFocusCleanClickable { onClick(gender) }
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = gender.text,
            fontSize = 16.sp,
            lineHeight = 20.8.sp,
            fontWeight = FontWeight(400),
            color = descriptionColor,
            textAlign = TextAlign.Center,
        )
    }
}

private object ProfileSettingColors {
    val selectedTextColor = Color(0xFFFFCF31)
    val defaultTextColor = Color(0xFFFFFFFF)

    val selectedGenderColor = Color(0xFFFFFFFF)
    val defaultGenderColor = Color(0xFF989898)

    val selectedBorderColor = Color(0xFFFFCF31)
    val defaultBorderColor = Color(0x00000000)

    val selectedBgColor = Color(0xFF222222)
    val defaultBgColor = Color(0xFF2C2C2C)
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
                androidx.compose.material.Text(
                    "선택",
                    color = MainColor.PrimaryYE
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                androidx.compose.material.Text("취소", color = MainColor.Greyscale19WH)
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