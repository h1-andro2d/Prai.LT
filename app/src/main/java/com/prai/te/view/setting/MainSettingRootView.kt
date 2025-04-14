package com.prai.te.view.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.MainViewModel

@Composable
internal fun MainSettingRootView(
    model: MainViewModel = viewModel(),
    repository: MainRepositoryViewModel = viewModel()
) {
    val isProfileVisible = model.isProfileSettingVisible.collectAsStateWithLifecycle()
    val nameText = repository.nameText.collectAsStateWithLifecycle()
    val ageText = repository.ageText.collectAsStateWithLifecycle()
    val gender = repository.selectedGender.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        MainSettingView(nameText.value, ageText.value, gender.value)
        AnimatedVisibility(
            visible = isProfileVisible.value,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(0))
        ) {
            MainProfileSettingView(nameText.value, ageText.value, gender.value)
        }
    }
}
