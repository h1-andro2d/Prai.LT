package com.prai.te.common

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.prai.te.R

internal object MainFont {
    val Pretendard = FontFamily(
        Font(R.font.pretendard_bold, FontWeight.Bold),
        Font(R.font.pretendard_extra_bold, FontWeight.ExtraBold),
        Font(R.font.pretendard_extra_light, FontWeight.ExtraLight),
        Font(R.font.pretendard_light, FontWeight.Light),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_normal, FontWeight.Normal),
        Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
        Font(R.font.pretendard_thin, FontWeight.Thin)
    )
}