package com.prai.te.view.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.common.MainFont
import com.prai.te.common.clearFocusCleanClickable
import com.prai.te.common.textDp
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.UserGender

@Composable
internal fun GenderSelection(
    selected: UserGender?,
    repository: MainRepositoryViewModel = viewModel()
) {
    val genders = listOf(UserGender.MALE, UserGender.FEMALE)

    val selectedTextColor = Color(0xFFFFCF31)
    val defaultTextColor = Color(0xFFFFFFFF)

    val selectedBorderColor = Color(0xFFFFCF31)
    val defaultBorderColor = Color(0x00000000)

    val selectedBgColor = Color(0xFF222222)
    val defaultBgColor = Color(0xFF2C2C2C)

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.5.dp)
    ) {
        genders.forEach { gender ->
            val isSelected = gender == selected

            val animatedDescriptionColor by animateColorAsState(
                targetValue = if (isSelected) {
                    selectedTextColor
                } else {
                    defaultTextColor
                },
                label = "genderDescColor"
            )
            val animatedBorderColor by animateColorAsState(
                targetValue = if (isSelected) {
                    selectedBorderColor
                } else {
                    defaultBorderColor
                },
                label = "genderBorderColor"
            )
            val animatedBackgroundColor by animateColorAsState(
                targetValue = if (isSelected) {
                    selectedBgColor
                } else {
                    defaultBgColor
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
            fontFamily = MainFont.Pretendard,
            fontSize = 16.textDp,
            lineHeight = 20.textDp,
            fontWeight = FontWeight(400),
            color = descriptionColor,
            textAlign = TextAlign.Center,
        )
    }
}