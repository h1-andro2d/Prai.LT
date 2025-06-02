package com.prai.te.view.call

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.prai.te.R
import com.prai.te.common.MainFont
import com.prai.te.common.cleanClickable
import com.prai.te.common.rippleClickable
import com.prai.te.common.textDp
import com.prai.te.model.MainVibeSettingItem
import com.prai.te.model.MainVoiceSettingItem
import com.prai.te.view.common.MainSaveButton
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.MainViewModel

@Preview
@Composable
internal fun AiSettingView(
    model: MainViewModel = viewModel(),
    repository: MainRepositoryViewModel = viewModel()
) {
    BackHandler {
        model.isAiSettingVisible.value = false
        repository.rollbackAiSetting()
    }

    LaunchedEffect(Unit) {
        repository.makeAiSettingCache()
    }

    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent("custom_screen_view", bundleOf("screen_name" to "voice_setting"))
    }

    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .background(
                color = Color(0xFF222222),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(R.drawable.main_setting_ai_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(22.dp)
            )
            Text(
                text = "AI 설정",
                textAlign = TextAlign.Center,
                fontFamily = MainFont.Pretendard,
                fontSize = 18.textDp,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.W600,
            )
        }
        Image(
            painter = painterResource(R.drawable.main_setting_x),
            contentDescription = null,
            modifier = Modifier
                .cleanClickable {
                    model.isAiSettingVisible.value = false
                    repository.rollbackAiSetting()
                }
                .padding(top = 22.dp, end = 20.dp)
                .align(Alignment.TopEnd)
                .size(22.dp)
        )
        AiSettingGrid(modifier = Modifier.padding(top = 88.dp, bottom = 79.dp))
        MainSaveButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {
                model.isAiSettingVisible.value = false
                repository.saveCurrentAiSetting()
            }
        )
    }
}

@Composable
internal fun AiSettingGrid(
    modifier: Modifier = Modifier,
    model: MainRepositoryViewModel = viewModel()
) {
    val selectedVoiceItem = model.selectedVoiceSettingItem.collectAsStateWithLifecycle()
    val selectedVibeItem = model.selectedVibeSettingItem.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        item {
            AiSettingGridTitle(
                text = "목소리",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            VoiceSettingGrid(
                selectedVoiceItem = selectedVoiceItem.value,
                onVoiceItemClick = { item ->
                    model.selectedVoiceSettingItem.value = item
                }
            )
        }
        item {
            AiSettingGridTitle(
                text = "속도",
                modifier = Modifier.padding(top = 30.dp, bottom = 16.dp)
            )
        }
        item {
            AiSettingSlider()
        }
        item {
            AiSettingGridTitle(
                text = "분위기",
                modifier = Modifier.padding(top = 30.dp, bottom = 16.dp)
            )
        }
        item {
            VibeSettingGrid(
                selectedVibeItem = selectedVibeItem.value,
                onVibeItemClick = { item ->
                    model.selectedVibeSettingItem.value = item
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AiSettingSlider(model: MainRepositoryViewModel = viewModel()) {
    val speed = model.selectedVoiceSpeed.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .background(color = Color(0xFF2C2C2C), shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(105.dp)
    ) {

        Slider(
            value = speed.value,
            onValueChange = { model.selectedVoiceSpeed.value = it },
            valueRange = 0f..1f,
            thumb = {
                Spacer(
                    Modifier
                        .size(16.dp)
                        .background(color = Color(0xFFFFCF31), CircleShape)
                )
            },
            track = { state ->
                Box {
                    val colors = SliderDefaults.colors(
                        activeTrackColor = Color(0xFFFFCF31),
                        inactiveTrackColor = Color(0x33FFFFFF),
                    )
                    SliderDefaults.Track(
                        colors = colors,
                        sliderState = state,
                        thumbTrackGapSize = 0.dp,
                        drawStopIndicator = {},
                        modifier = Modifier.height(6.dp),
                    )
                }
            },
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 25.dp, bottom = 10.dp)
                .fillMaxWidth()
                .height(20.dp),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "느림",
                fontFamily = MainFont.Pretendard,
                fontSize = 16.textDp,
                lineHeight = 20.textDp,
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
            )
            Text(
                text = "중간",
                fontFamily = MainFont.Pretendard,
                fontSize = 16.textDp,
                lineHeight = 20.textDp,
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
            )
            Text(
                text = "빠름",
                fontFamily = MainFont.Pretendard,
                fontSize = 16.sp,
                lineHeight = 20.8.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
            )
        }

    }
}

@Composable
private fun VoiceSettingGrid(
    selectedVoiceItem: MainVoiceSettingItem?,
    onVoiceItemClick: (MainVoiceSettingItem) -> Unit
) {
    val allVoiceItems = MainVoiceSettingItem.entries.toList()
    val rows = allVoiceItems.chunked(3)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.5.dp)
            ) {
                rowItems.forEach { item ->
                    val isSelected = item == selectedVoiceItem

                    val animatedDescriptionColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            AiSettingColors.selectedTextColor
                        } else {
                            AiSettingColors.defaultTextColor
                        },
                        label = "voiceDescColor"
                    )
                    val animatedGenderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            AiSettingColors.selectedGenderColor
                        } else {
                            AiSettingColors.defaultGenderColor
                        },
                        label = "voiceGenderColor"
                    )
                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            AiSettingColors.selectedBorderColor
                        } else {
                            AiSettingColors.defaultBorderColor
                        },
                        label = "voiceBorderColor"
                    )
                    val animatedBackgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            AiSettingColors.selectedBgColor
                        } else {
                            AiSettingColors.defaultBgColor
                        },
                        label = "voiceBackgroundColor"
                    )

                    VoiceSettingItem(
                        item = item,
                        descriptionColor = animatedDescriptionColor,
                        genderColor = animatedGenderColor,
                        borderColor = animatedBorderColor,
                        bgColor = animatedBackgroundColor,
                        onClick = { onVoiceItemClick(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                val emptySlots = 3 - rowItems.size
                if (emptySlots > 0) {
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun VibeSettingGrid(
    selectedVibeItem: MainVibeSettingItem?,
    onVibeItemClick: (MainVibeSettingItem) -> Unit
) {
    val allVibeItems = MainVibeSettingItem.entries.toList()
    val rows = allVibeItems.chunked(3)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.5.dp)
            ) {
                rowItems.forEach { item ->
                    val isSelected = (item == selectedVibeItem)
                    val animatedDescriptionColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            AiSettingColors.selectedTextColor
                        } else {
                            AiSettingColors.defaultTextColor
                        },
                        label = "vibeDescColor"
                    )
                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            AiSettingColors.selectedBorderColor
                        } else {
                            AiSettingColors.defaultBorderColor
                        },
                        label = "vibeBorderColor"
                    )
                    val animatedBackgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            AiSettingColors.selectedBgColor
                        } else {
                            AiSettingColors.defaultBgColor
                        },
                        label = "vibeBackgroundColor"
                    )

                    VibeSettingItem(
                        item = item,
                        descriptionColor = animatedDescriptionColor,
                        borderColor = animatedBorderColor,
                        bgColor = animatedBackgroundColor,
                        onClick = { onVibeItemClick(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                val emptySlots = 3 - rowItems.size
                if (emptySlots > 0) {
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceSettingItem(
    item: MainVoiceSettingItem,
    descriptionColor: Color,
    genderColor: Color,
    borderColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .cleanClickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = item.description,
            fontFamily = MainFont.Pretendard,
            fontSize = 16.sp,
            lineHeight = 20.8.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight(400),
            color = descriptionColor,
            textAlign = TextAlign.Center,
        )
        Text(
            text = item.gender.text,
            fontFamily = MainFont.Pretendard,
            fontSize = 16.sp,
            lineHeight = 20.8.sp,
            fontWeight = FontWeight(400),
            color = genderColor
        )
    }
}

@Composable
private fun VibeSettingItem(
    item: MainVibeSettingItem,
    descriptionColor: Color,
    borderColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(60.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(60.dp))
            .cleanClickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = item.description,
            fontFamily = MainFont.Pretendard,
            fontSize = 16.sp,
            lineHeight = 20.8.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight(400),
            color = descriptionColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AiSettingGridTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontFamily = MainFont.Pretendard,
        lineHeight = 23.4.sp,
        fontWeight = FontWeight(600),
        color = Color(0xFFFFCF31),
        modifier = modifier
    )
}

private object AiSettingColors {
    val selectedTextColor = Color(0xFFFFCF31)
    val defaultTextColor = Color(0xFFFFFFFF)

    val selectedGenderColor = Color(0xFFFFFFFF)
    val defaultGenderColor = Color(0xFF989898)

    val selectedBorderColor = Color(0xFFFFCF31)
    val defaultBorderColor = Color(0x00000000)

    val selectedBgColor = Color(0xFF222222)
    val defaultBgColor = Color(0xFF2C2C2C)
}