package com.prai.te.view.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.common.FadeView
import com.prai.te.view.model.MainViewModel

@Composable
internal fun MainSettingRootView(model: MainViewModel = viewModel()) {
    val isProfileVisible = model.isProfileSettingVisible.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        MainSettingView()
        FadeView(visible = isProfileVisible.value) {
            MainProfileSettingView()
        }
    }
}
