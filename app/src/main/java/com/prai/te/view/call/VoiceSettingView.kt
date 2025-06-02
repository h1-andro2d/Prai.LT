package com.prai.te.view.call

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.collection.mutableFloatSetOf
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.R
import com.prai.te.common.MainColor
import com.prai.te.common.MainFont
import com.prai.te.common.VerticalGap
import com.prai.te.common.cleanClickable
import com.prai.te.common.textDp
import com.prai.te.model.FriendGender
import com.prai.te.model.FriendTone
import com.prai.te.model.TeacherTone
import com.prai.te.view.common.CommonConfirmButton
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.model.MainVoiceViewModel

@Preview
@Composable
internal fun VoiceSettingView(
    model: MainVoiceViewModel = viewModel(),
    mainModel: MainViewModel = viewModel()
) {
    val isFriendMode by model.isFriendMode.collectAsStateWithLifecycle()
    val friendVoiceLevel by model.friendVoiceLevel.collectAsStateWithLifecycle()
    val friendGender by model.friendGender.collectAsStateWithLifecycle()
    val friendTone by model.friendTone.collectAsStateWithLifecycle()
    val teacherVoiceLevel by model.teacherVoiceLevel.collectAsStateWithLifecycle()
    val teacherTone by model.teacherTone.collectAsStateWithLifecycle()

    val isChanged by remember(
        isFriendMode,
        friendVoiceLevel,
        friendGender,
        friendTone,
        teacherVoiceLevel,
        teacherTone
    ) {
        derivedStateOf {
            isFriendMode != model.isFriendModeCache ||
                    friendVoiceLevel != model.friendVoiceLevelCache ||
                    friendGender != model.friendGenderCache ||
                    friendTone != model.friendToneCache ||
                    teacherVoiceLevel != model.teacherVoiceLevelCache ||
                    teacherTone != model.teacherToneCache
        }
    }

    BackHandler {
        mainModel.isAiSettingVisible.value = false
        model.rollbackVoiceSetting()
    }

    LaunchedEffect(Unit) {
        model.makeVoiceSettingCache()
    }

    Box(
        modifier = Modifier
            .padding(top = 15.dp)
            .background(
                color = Color(0xFF222222),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize()
    ) {
        Header()

        ModeSelection(isFriendMode)
        Box(modifier = Modifier.padding(top = 147.dp, bottom = 76.dp)) {
            if (isFriendMode) {
                FriendLazyColumn()
            } else {
                TeacherLazyColumn()
            }
        }
        Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 6.dp)
                .align(
                    Alignment.BottomCenter
                )
        ) {
            CommonConfirmButton(text = "대화 친구 설정완료", isChanged, onClick = {
                if (isChanged) {
                    mainModel.isAiSettingVisible.value = false
                    model.saveCurrentVoiceSetting()
                }
            })
        }
    }
}

@Preview
@Composable
private fun Header(
    model: MainVoiceViewModel = viewModel(),
    mainModel: MainViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(R.drawable.voice_setting_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(22.dp)
            )
            Text(
                text = "대화 친구 설정",
                fontSize = 18.textDp,
                lineHeight = 22.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = MainColor.OnSurfaceWH,
                textAlign = TextAlign.Center,
            )
        }

        Image(
            painter = painterResource(R.drawable.main_setting_x),
            contentDescription = null,
            modifier = Modifier
                .cleanClickable {
                    mainModel.isAiSettingVisible.value = false
                    model.rollbackVoiceSetting()
                }
                .padding(end = 20.dp)
                .align(Alignment.CenterEnd)
                .size(24.dp)
        )
    }
}

@Composable
private fun ModeSelection(isFriendMode: Boolean, model: MainVoiceViewModel = viewModel()) {
    val friendBg = if (isFriendMode) MainColor.PrimaryYE else MainColor.Transparent
    val teacherBg = if (isFriendMode) MainColor.Transparent else MainColor.PrimaryYE
    val friendTextColor = if (isFriendMode) MainColor.Greyscale01BK else MainColor.Greyscale17WH
    val teacherTextColor = if (isFriendMode) MainColor.Greyscale17WH else MainColor.Greyscale01BK

    Box(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 88.dp, bottom = 15.dp)
            .fillMaxWidth()
            .height(44.dp)
            .background(color = MainColor.Greyscale07BK, shape = RoundedCornerShape(60.dp))
    ) {
        Row {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(44.dp)
                    .background(color = friendBg, shape = RoundedCornerShape(60.dp))
                    .cleanClickable {
                        model.isFriendMode.value = true
                    }
            ) {
                Text(
                    text = "친구 버전",
                    fontSize = 16.textDp,
                    lineHeight = 16.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(700),
                    color = friendTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(44.dp)
                    .background(color = teacherBg, shape = RoundedCornerShape(60.dp))
                    .cleanClickable {
                        model.isFriendMode.value = false
                    }
            ) {
                Text(
                    text = "선생님 버전",
                    fontSize = 16.textDp,
                    lineHeight = 16.textDp,
                    fontFamily = MainFont.Pretendard,
                    fontWeight = FontWeight(700),
                    color = teacherTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview
@Composable
private fun FriendLazyColumn(model: MainVoiceViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val friendTone = model.friendTone.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        VerticalGap(22)
        CategoryText("목소리 속도")
        VerticalGap(12)
        AiSettingSlider(model.friendVoiceLevel.value) {
            model.friendVoiceLevel.value = it
        }
        VerticalGap(26)
        Divider()
        VerticalGap(26)
        CategoryText("성별")
        VerticalGap(12)
        FriendGenderSelection()
        VerticalGap(26)
        Divider()
        VerticalGap(26)
        CategoryText("직업")
        VerticalGap(12)
        FriendToneGrid(friendTone.value) {
            model.friendTone.value = it
        }
        VerticalGap(12)
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview
@Composable
private fun TeacherLazyColumn(model: MainVoiceViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val teacherTone = model.teacherTone.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        VerticalGap(22)
        CategoryText("목소리 속도")
        VerticalGap(12)
        AiSettingSlider(model.teacherVoiceLevel.value) {
            model.teacherVoiceLevel.value = it
        }
        VerticalGap(26)
        Divider()
        VerticalGap(38)
        TeacherToneGrid(teacherTone.value) {
            model.teacherTone.value = it
        }
        VerticalGap(12)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AiSettingSlider(value: Float = 1.0f, onValue: (Float) -> Unit = {}) {
    var speed by remember { mutableFloatStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp)
    ) {
        Slider(
            value = speed,
            onValueChange = {
                speed = it
                onValue.invoke(it)
            },
            valueRange = 0.7f..1.3f,
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
                .fillMaxWidth()
                .height(20.dp),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                .fillMaxWidth()
        ) {
            SpeedText("0.7x", speed < 0.75f)
            SpeedText("0.8x", 0.75f <= speed && speed < 0.85f)
            SpeedText("0.9x", 0.85f <= speed && speed < 0.95f)
            SpeedText("1.0x", 0.95f <= speed && speed < 1.05f)
            SpeedText("1.1x", 1.05f <= speed && speed < 1.15f)
            SpeedText("1.2x", 1.15f <= speed && speed < 1.25f)
            SpeedText("1.3x", 1.25f <= speed)
        }
    }
}

@Composable
private fun SpeedText(text: String, isSelected: Boolean) {

    Text(
        text = text,
        fontSize = 14.textDp,
        lineHeight = 18.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(600),
        color = if (isSelected) MainColor.OnSurfaceWH else MainColor.Greyscale18WH,
    )
}

@Composable
private fun CategoryText(text: String) {
    Text(
        text = text,
        fontSize = 16.textDp,
        lineHeight = 20.textDp,
        fontFamily = MainFont.Pretendard,
        fontWeight = FontWeight(600),
        color = MainColor.OnSurfaceWH,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun Divider() {
    Spacer(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MainColor.Greyscale08BK)
    )
}

@Preview
@Composable
private fun FriendGenderSelection(model: MainVoiceViewModel = viewModel()) {
    val gender = model.friendGender.collectAsStateWithLifecycle()
    val isMale = gender.value == FriendGender.MALE
    val maleBgColor = if (isMale) MainColor.PrimaryYE else MainColor.Greyscale07BK
    val maleTextColor = if (isMale) MainColor.OnSurfaceBK else MainColor.Greyscale20WH

    val femaleBgColor = if (isMale) MainColor.Greyscale07BK else MainColor.PrimaryYE
    val femaleTextColor = if (isMale) MainColor.Greyscale20WH else MainColor.OnSurfaceBK


    Row(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box(
            modifier = Modifier
                .padding(end = 7.dp)
                .width(105.dp)
                .height(45.dp)
                .background(color = maleBgColor, shape = RoundedCornerShape(size = 60.dp))
                .padding(end = 7.dp)
                .cleanClickable {
                    model.friendGender.value = FriendGender.MALE
                }
        ) {
            Text(
                text = "남성",
                fontSize = 16.textDp,
                lineHeight = 20.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = maleTextColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .padding(end = 7.dp)
                .width(105.dp)
                .height(45.dp)
                .background(color = femaleBgColor, shape = RoundedCornerShape(size = 60.dp))
                .cleanClickable {
                    model.friendGender.value = FriendGender.FEMALE
                }
        ) {
            Text(
                text = "여성",
                fontSize = 16.textDp,
                lineHeight = 20.textDp,
                fontFamily = MainFont.Pretendard,
                fontWeight = FontWeight(600),
                color = femaleTextColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private object VoiceSettingColors {
    val selectedTextColor = MainColor.PrimaryYE
    val defaultTextColor = MainColor.Greyscale20WH

    val selectedGenderColor = MainColor.Greyscale20WH
    val defaultGenderColor = MainColor.Greyscale17WH

    val selectedBorderColor = MainColor.PrimaryYE
    val defaultBorderColor = MainColor.Transparent

    val selectedBgColor = MainColor.Greyscale02BK
    val defaultBgColor = MainColor.Greyscale07BK
}

@Preview
@Composable
private fun FriendToneGrid(
    selectedVoiceItem: FriendTone? = null,
    onVoiceItemClick: (FriendTone) -> Unit = {}
) {
    val allVoiceItems = FriendTone.entries.toList()
    val rows = allVoiceItems.chunked(3)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    val isSelected = item == selectedVoiceItem

                    val animatedDescriptionColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedTextColor
                        } else {
                            VoiceSettingColors.defaultTextColor
                        },
                        label = "voiceDescColor"
                    )
                    val animatedGenderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedGenderColor
                        } else {
                            VoiceSettingColors.defaultGenderColor
                        },
                        label = "voiceGenderColor"
                    )
                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedBorderColor
                        } else {
                            VoiceSettingColors.defaultBorderColor
                        },
                        label = "voiceBorderColor"
                    )
                    val animatedBackgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedBgColor
                        } else {
                            VoiceSettingColors.defaultBgColor
                        },
                        label = "voiceBackgroundColor"
                    )

                    FriendToneItem(
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
private fun FriendToneItem(
    item: FriendTone,
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
            fontSize = 16.textDp,
            lineHeight = 20.textDp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight(400),
            color = descriptionColor,
            textAlign = TextAlign.Center,
        )
    }
}


@Preview
@Composable
private fun TeacherToneGrid(
    selectedVoiceItem: TeacherTone? = null,
    onVoiceItemClick: (TeacherTone) -> Unit = {}
) {
    val allVoiceItems = TeacherTone.entries.toList()
    val rows = allVoiceItems.chunked(3)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.5.dp)
            ) {
                rowItems.forEach { item ->
                    val isSelected = item == selectedVoiceItem

                    val animatedDescriptionColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedTextColor
                        } else {
                            VoiceSettingColors.defaultTextColor
                        },
                        label = "voiceDescColor"
                    )
                    val animatedGenderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedGenderColor
                        } else {
                            VoiceSettingColors.defaultGenderColor
                        },
                        label = "voiceGenderColor"
                    )
                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedBorderColor
                        } else {
                            VoiceSettingColors.defaultBorderColor
                        },
                        label = "voiceBorderColor"
                    )
                    val animatedBackgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            VoiceSettingColors.selectedBgColor
                        } else {
                            VoiceSettingColors.defaultBgColor
                        },
                        label = "voiceBackgroundColor"
                    )

                    TeacherToneItem(
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
private fun TeacherToneItem(
    item: TeacherTone,
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
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = item.description,
            fontFamily = MainFont.Pretendard,
            fontSize = 16.textDp,
            lineHeight = 20.textDp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight(400),
            color = descriptionColor,
            textAlign = TextAlign.Center,
        )
        Text(
            text = item.gender.text,
            fontSize = 14.textDp,
            lineHeight = 18.textDp,
            fontFamily = MainFont.Pretendard,
            fontWeight = FontWeight(400),
            color = genderColor,
        )
    }
}