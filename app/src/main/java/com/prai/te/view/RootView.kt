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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.te.common.CutFadeView
import com.prai.te.common.FadeView
import com.prai.te.common.MainLogger
import com.prai.te.common.MainNavigator
import com.prai.te.common.cleanClickable
import com.prai.te.model.MainEvent
import com.prai.te.model.MainIntroState
import com.prai.te.view.call.CallView
import com.prai.te.view.common.dialog.BillingSuccessDialog
import com.prai.te.view.common.BillingView
import com.prai.te.view.common.dialog.OneButtonDialog
import com.prai.te.view.common.ServerLoadingView
import com.prai.te.view.common.SomethingOutView
import com.prai.te.view.common.dialog.TwoButtonDialog
import com.prai.te.view.intro.LoginView
import com.prai.te.view.intro.OnboardingView
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.premium.MyMembershipScreen
import com.prai.te.view.setting.MainSettingRootView

@Composable
internal fun RootView(model: MainViewModel = viewModel()) {
    val isSettingVisible = model.isMainSettingVisible.collectAsStateWithLifecycle()
    val introState = model.introState.collectAsStateWithLifecycle()
    val billingVisible = model.isBillingVisible.collectAsStateWithLifecycle()
    val isMyMembership = model.isMyMembershipVisible.collectAsStateWithLifecycle()
    val outCase = model.outCase.collectAsStateWithLifecycle()

    MainLogger.View("RootView").log("RootView, introState: $introState")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
            .cleanClickable {}
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        CutFadeView(introState.value == MainIntroState.LOGIN, cutEnd = true) {
            LoginView()
        }
//        FadeView(introState.value == MainIntroState.ONBOARDING) {
//            OnboardingView()
//        }
        if (introState.value == MainIntroState.ONBOARDING) {
            OnboardingView()
        }
//        if (introState.value == MainIntroState.LOGIN) {
//            LoginView()
//        }
//        if (introState.value == MainIntroState.ONBOARDING) {
//            OnboardingView()
//        }
        if(introState.value == MainIntroState.DONE) {
            CallView()
            if (isSettingVisible.value) {
                Box (modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)){
                    MainSettingRootView()
                }
            }
//            CutFadeView(
//                visible = isSettingVisible.value,
//                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
//                cutEnd = true
//            ) {
//                MainSettingRootView()
//            }
        }
        if (introState.value != MainIntroState.SPLASH) {
            LoadingBox()
        }

        if (billingVisible.value) {
            BillingView()
        }
        if (isMyMembership.value) {
            MyMembershipScreen()
        }
        outCase.value?.let { SomethingOutView(it) }
        DialogBox()
    }
}

@Composable
private fun DialogBox(model: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val introEndDialog = model.introEndDialog.collectAsStateWithLifecycle()
    val forceUpdateDialog = model.forceUpdateDialog.collectAsStateWithLifecycle()
    val isServerError = model.isServerErrorDialog.collectAsStateWithLifecycle()
    val isLogoutDialog = model.isLogoutDialog.collectAsStateWithLifecycle()
    val isDeleteUserDialog = model.isDeleteUserDialog.collectAsStateWithLifecycle()
    val isBillingSuccessDialog = model.isBillingSuccessDialog.collectAsStateWithLifecycle()
    val isRecoverSuccessDialog = model.isRecoverSuccessDialog.collectAsStateWithLifecycle()
    val oneButtonDialogData = model.oneButtonDialogData.collectAsStateWithLifecycle()

    FadeView(introEndDialog.value, modifier = Modifier.fillMaxSize()) {
        TwoButtonDialog(
            "앱을 종료하시겠어요?",
            "PRAI랑 대화하려면 먼저 로그인이 필요해요.",
            "종료",
            "취소",
            onLeftButtonClick = {
                model.onServiceEnd()
                model.introEndDialog.value = false
            },
            onRightButtonClick = { model.introEndDialog.value = false },
            onBackHandler = { model.introEndDialog.value = false },
            drawBackground = true
        )
    }

    FadeView(forceUpdateDialog.value, modifier = Modifier.fillMaxSize()) {
        OneButtonDialog(
            "PRAI가 더 똑똑해졌어요!",
            "새로운 기능을 쓰기 위해 업데이트가 필요해요.\n 오늘도 말하기 연습, 함께 해요 :)",
            "PRAI 업데이트 하기",
            onMainButtonClick = { MainNavigator.openAppStore(context) },
            onBackHandler = { },
            drawBackground = true
        )
    }

    FadeView(isServerError.value, modifier = Modifier.fillMaxSize()) {
        OneButtonDialog(
            "일시적인 에러 발생",
            "잠시 후 다시 시도하거나,\n인터넷 연결을 확인해 주세요 :)",
            "확인",
            onMainButtonClick = { model.isServerErrorDialog.value = false },
            onBackHandler = { model.isServerErrorDialog.value = false },
            drawBackground = true
        )
    }

    FadeView(isLogoutDialog.value, modifier = Modifier.fillMaxSize()) {
        TwoButtonDialog(
            "로그아웃하시겠어요?",
            "PRAI는 여기서 기다릴게요 :)\n언제든 다시 오셔도 돼요!",
            "로그아웃",
            "취소",
            onLeftButtonClick = {
                model.isLogoutDialog.value = false
                model.introState.value = MainIntroState.LOGIN
                model.dispatchEvent(MainEvent.LogoutRequest)
                model.launchDelayed(500L) {
                    model.isMainSettingVisible.value = false
                    model.isProfileSettingVisible.value = false
                    model.initializeData()
                }
            },
            onRightButtonClick = { model.isLogoutDialog.value = false },
            onBackHandler = { model.isLogoutDialog.value = false },
            drawBackground = true
        )
    }

    if(isDeleteUserDialog.value) {
        TwoButtonDialog(
            "헤어지기엔 너무 아쉬워요.",
            "PRAI와의 모든 기록과 정보는\n삭제되며 복구가 불가능해요.\n잠시 쉬고 싶다면 로그아웃을 추천드려요.\n\n정말 탈퇴하시겠어요?",
            "떠날래요",
            "더 써볼래요",
            onLeftButtonClick = {
                model.isDeleteUserDialog.value = false
                model.introState.value = MainIntroState.LOGIN
                model.dispatchEvent(MainEvent.DeleteUserRequest)
                model.launchDelayed(500L) {
                    model.isMainSettingVisible.value = false
                    model.isProfileSettingVisible.value = false
                    model.initializeData()
                }
            },
            onRightButtonClick = { model.isDeleteUserDialog.value = false },
            onBackHandler = { model.isDeleteUserDialog.value = false },
            drawBackground = true,
            reverseColor = true
        )
    }

    FadeView(oneButtonDialogData.value != null, modifier = Modifier.fillMaxSize()) {
        val data = oneButtonDialogData.value ?: return@FadeView
        OneButtonDialog(
            data.title,
            data.message,
            data.buttonText,
            onMainButtonClick = { model.oneButtonDialogData.value = null },
            onBackHandler = { model.oneButtonDialogData.value = null },
            drawBackground = true,
        )
    }

    if (isBillingSuccessDialog.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            BillingSuccessDialog(
                onMainButtonClick = {
                    model.clearView()
                    model.isPremiumUser.value = true
                },
                onBackHandler = {
                    model.isBillingSuccessDialog.value = false
                },
                drawBackground = true
            )
        }
    }


    if (isRecoverSuccessDialog.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            BillingSuccessDialog(
                titleText = "구독 복원 완료!\nPRAI가 영어 파트너가 되어줄게요 :)",
                onMainButtonClick = {
                    model.clearView()
                    model.isPremiumUser.value = true
                },
                onBackHandler = {
                    model.isRecoverSuccessDialog.value = false
                },
                drawBackground = true
            )
        }
    }
}

@Composable
private fun LoadingBox(model: MainViewModel = viewModel()) {
    val isServerBlocking = model.isRegisterProcessing.collectAsStateWithLifecycle()
    val isLoginProcessing = model.isLoginProcessing.collectAsStateWithLifecycle()

    CutFadeView(
        isLoginProcessing.value,
        cutEnd = true,
        modifier = Modifier.fillMaxSize()
    ) {
        ServerLoadingView(700L)
    }
    FadeView(
        isServerBlocking.value,
        modifier = Modifier.fillMaxSize()
    ) {
        ServerLoadingView(0L)
    }
}