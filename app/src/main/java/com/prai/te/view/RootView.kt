package com.prai.te.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.common.FadeView
import com.prai.te.view.call.CallView
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.setting.MainSettingRootView

@Composable
internal fun RootView(model: MainViewModel = viewModel()) {
    val isSettingVisible = model.isMainSettingVisible.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        CallView()
        FadeView(
            visible = isSettingVisible.value,
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            MainSettingRootView()
        }
    }
}