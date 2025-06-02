package com.prai.te

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.billingclient.api.Purchase
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.prai.te.auth.MainAuthManager
import com.prai.te.billing.MainBillingManager
import com.prai.te.common.MainCodec
import com.prai.te.common.MainLogger
import com.prai.te.media.MainFileManager
import com.prai.te.media.MainPlayer
import com.prai.te.media.MainRecorder
import com.prai.te.media.MainVolumeReader
import com.prai.te.model.MainBillingState
import com.prai.te.model.MainCallState
import com.prai.te.model.MainEvent
import com.prai.te.model.MainIntroState
import com.prai.te.model.MainPremiumPlan
import com.prai.te.permission.MainPermission
import com.prai.te.permission.MainPermissionHandler
import com.prai.te.retrofit.MainCallSegment
import com.prai.te.retrofit.MainRetrofit
import com.prai.te.retrofit.MainRetrofit.Event
import com.prai.te.security.MainSecurityManager
import com.prai.te.view.RootView
import com.prai.te.view.model.BillingMessage
import com.prai.te.view.model.CallSegmentItem
import com.prai.te.view.model.MainRepositoryViewModel
import com.prai.te.view.model.MainViewModel
import com.prai.te.view.model.UserGender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val viewModel: MainViewModel by viewModels()
    private val repository: MainRepositoryViewModel by viewModels()
    private val authManager: MainAuthManager by lazy { MainAuthManager(scope) }
    private val recorder: MainRecorder by lazy { MainRecorder(scope) }
    private val retrofit: MainRetrofit by lazy { MainRetrofit(scope, authManager) }
    private val player: MainPlayer by lazy { MainPlayer(scope) }
    private val billingManager: MainBillingManager by lazy { MainBillingManager(this, retrofit) }
    private val volumeReader: MainVolumeReader by lazy { MainVolumeReader(scope) }

    private val transparent = Color.Transparent.toArgb()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.introState.value == MainIntroState.SPLASH
        }

        initialize()
        setContent { RootView() }
    }

    override fun onDestroy() {
        scope.cancel()
        recorder.stop()
        volumeReader.stop()
        player.stop()
        MainFileManager.deleteAllAudioFiles(this)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        when (requestCode) {
            MainPermission.AUDIO.code -> scope.launch { handleRecordStartEvent() }
            else -> Unit
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initialize() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(transparent),
            navigationBarStyle = SystemBarStyle.dark(transparent)
        )
        overridePendingTransition(0, 0)
        authManager.initialize(this)
        billingManager.initialize()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        collectMainEvent()
        collectAuthEvent()
        collectRecorderEvent()
        collectRetrofitEvent()
        collectPlayerEvent()
        collectVolumeReaderEvent()
        collectViewState()
        collectRepositoryEvent()
        collectBillingEvent()
        checkMinimumVersion()
        checkPremiumUser()
        MainScope().launch { checkCredentialState {} }
        MainSecurityManager.checkAppIntegrity(this)
    }

    private suspend fun checkCredentialState(onEnd: () -> Unit) {
        if (MainAuthManager.isConnected().not()) {
            withContext(Dispatchers.Main) {
                authManager.disconnect()
                viewModel.introState.value = MainIntroState.LOGIN
            }
            MainLogger.Activity.log("checkCredentialState: not connected")
            return
        }
        withContext(Dispatchers.IO) {
            val token = authManager.getAuthToken()
            if (token == null) {
                MainLogger.Activity.log("checkCredentialState: fail to get AuthToken")
                withContext(Dispatchers.Main) {
                    authManager.disconnect()
                    viewModel.isServerErrorDialog.value = true
                    viewModel.introState.value = MainIntroState.LOGIN
                    onEnd.invoke()
                }
                return@withContext
            }
            val event = retrofit.getUserInfoBlocking(token)
            if (event is MainRetrofit.Event.UserInfoNotFound) {
                MainLogger.Activity.log("checkCredentialState: user info not found")
                withContext(Dispatchers.Main) {
                    viewModel.introState.value = MainIntroState.ONBOARDING
                    onEnd.invoke()
                }
                return@withContext
            }
            if (event is MainRetrofit.Event.UserInfoResponse) {
                MainLogger.Activity.log("checkCredentialState: user info success")
                withContext(Dispatchers.Main) {
                    retrofit.getPremiumInfoSuspend()
                    repository.updateServerUserInfo(event.response)
                    viewModel.introState.value = MainIntroState.DONE
                    onEnd.invoke()
                }
                return@withContext
            } else {
                MainLogger.Activity.log("checkCredentialState: USER API ERROR")
                withContext(Dispatchers.Main) {
                    authManager.disconnect()
                    viewModel.isServerErrorDialog.value = true
                    viewModel.introState.value = MainIntroState.LOGIN
                    onEnd.invoke()
                }
                return@withContext
            }
        }
    }

    private fun checkMinimumVersion() {
        retrofit.getMinimumVersion()
    }

    private fun checkPremiumUser() {
        retrofit.getPremiumInfo()
    }

    private fun collectAuthEvent() {
        MainScope().launch {
            authManager.event.collect { event ->
                handleAuthEvent(event)
            }
        }
    }

    private suspend fun handleAuthEvent(event: MainAuthManager.Event) {
        when (event) {
            is MainAuthManager.Event.Connect -> {
                checkCredentialState {
                    viewModel.isLoginProcessing.value = false
                }
            }

            MainAuthManager.Event.Disconnect -> {

            }

            is MainAuthManager.Event.AuthError,
            MainAuthManager.Event.NetworkError,
            MainAuthManager.Event.Error -> { // TODO: Divide cancel logic and error logic
                checkGooglePlayServices()
                delay(1200L)
                authManager.disconnect()
                withContext(Dispatchers.Main) {
                    viewModel.isServerErrorDialog.value = true
                    viewModel.isLoginProcessing.value = false
                }
            }

            MainAuthManager.Event.Cancel -> {
                delay(1200L)
                withContext(Dispatchers.Main) {
                    viewModel.isLoginProcessing.value = false
                }
            }

            MainAuthManager.Event.NoCredential -> {
                authManager.disconnect()
                startAddAccountActivity()
            }

            else -> Unit
        }
    }

    private fun collectViewState() {
        MainScope().launch {
            viewModel.introState.collect {
                MainLogger.Activity.log("collectViewState, intro: $it")
                when (it) {
                    MainIntroState.DONE -> {
                        MainPermissionHandler.requestPermissions(
                            this@MainActivity,
                            MainPermission.AUDIO
                        )
                        requestConversationList()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun collectMainEvent() {
        MainLogger.Activity.log("Setting up event collection")
        MainScope().launch {
            MainLogger.Activity.log("Starting to collect events")
            try {
                viewModel.event.collect { event ->
                    MainLogger.Activity.log("Collected event: $event")
                    scope.launch {
                        try {
                            handleMainEvent(event)
                        } catch (exception: Exception) {
                            MainLogger.Activity.log(
                                exception,
                                "Error handling event: ${exception.message}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                MainLogger.Activity.log(e, "Error collecting events: ${e.message}")
            }
        }
    }

    private suspend fun handleMainEvent(event: MainEvent) {
        when (event) {
            is MainEvent.RecordStart -> handleRecordStartEvent()
            is MainEvent.RecordStop -> {
                recorder.stop()
                volumeReader.stop()
            }

            is MainEvent.RecordCancel -> {
                recorder.cancel()
                volumeReader.stop()
            }

            is MainEvent.ServiceEnd -> finish()

            is MainEvent.PlayStart -> player.start(event.path)
            is MainEvent.GoogleLoginRequest -> {
                withContext(Dispatchers.Main) {
                    viewModel.isLoginProcessing.value = true
                }
                authManager.connect(this@MainActivity)
            }

            is MainEvent.LogoutRequest -> {
                authManager.disconnect()
                withContext(Dispatchers.Main) {
                    repository.reset()
                }
            }

            is MainEvent.DeleteUserRequest -> {
                val token = authManager.getAuthToken()
                if (token != null) {
                    authManager.disconnect()
                    retrofit.deleteUser(token)
                    withContext(Dispatchers.Main) {
                        repository.reset()
                    }
                }
            }

            is MainEvent.SomethingOutClick -> {
                retrofit.sendCancellationReason(event.message)
            }

            is MainEvent.RegisterUserRequest -> {
                withContext(Dispatchers.Main) {
                    viewModel.isRegisterProcessing.value = true
                }
                val token = authManager.getAuthToken()
                if (token != null) {
                    retrofit.registerUser(
                        token,
                        repository.nameText.value,
                        repository.ageText.value,
                        repository.selectedGender.value?.code ?: UserGender.MALE.code,
                    )
                }
            }

            is MainEvent.CallStart -> {
                handleFirstCallStart()
            }

            is MainEvent.CallEnd -> player.stop()
            is MainEvent.ConversationListOpen -> requestConversationList()
            is MainEvent.ConversationOpen -> retrofit.getConversation(event.id)
            is MainEvent.TranslationRequest -> retrofit.getTranslation(event.text)
            is MainEvent.BillingItemClick -> {
                withContext(Dispatchers.IO) {
                    doBilling(event.plan)
                }
            }

            is MainEvent.BillingRecoverClick -> {
                withContext(Dispatchers.IO) {
                    recoverBilling()
                }
            }
        }
    }

    private suspend fun handleFirstCallStart() {
        val result = retrofit.getPremiumInfoSuspend()
        if (result == null) {
            viewModel.isServerErrorDialog.value = true
            return
        }
        if (result.premium) {
            startPremiumCall()
        } else {
            startFreeTrialCall()
        }
    }

    private suspend fun startPremiumCall() {
        viewModel.isFreeTrialCall.value = false
        startActualCall()
    }

    private suspend fun startActualCall() {
        viewModel.onCallStart()
        val token = authManager.getAuthToken()
        val userId = repository.userId
        if (token != null && userId != null) {
            retrofit.sendFirstCallRequest(
                token,
                userId,
                repository.selectedVoiceSettingItem.value.code,
                repository.selectedVibeSettingItem.value.code,
                repository.selectedVoiceSpeed.value
            )
        } else {
            withContext(Dispatchers.Main) {
                viewModel.callState.value = MainCallState.None
                viewModel.isServerErrorDialog.value = true
            }
        }
    }

    private suspend fun startFreeTrialCall() {
        viewModel.isFreeTrialCall.value = true
        if (repository.canStartFreeTrial()) {
            startActualCall()
        } else {
            viewModel.isBillingVisible.value = true
        }
    }

    private suspend fun doBilling(plan: MainPremiumPlan) {
        viewModel.billingMessage.value = BillingMessage.BILLING_PROCESSING
        val result = retrofit.getPremiumInfoSuspend()
        if (result == null) {
            showBillingMessage(BillingMessage.PRAI_SERVER_ERROR)
            return
        }
        if (result.premium) {
            showBillingMessage(BillingMessage.ALREADY_PREMIUM)
            return
        } else {
            if (viewModel.billingItems.value.isEmpty()) {
                billingManager.initialize()
                showBillingMessage(BillingMessage.GOOGLE_PLAY_NO_ITEM)
                return
            }
            val purchaseList = billingManager.queryPurchasesSuspend()
            if (purchaseList?.any { it.purchaseState == Purchase.PurchaseState.PENDING } == true) {
                showBillingMessage(BillingMessage.PENDING)
                return
            }
            if (purchaseList?.any { it.purchaseState == Purchase.PurchaseState.PURCHASED } == true) {
                showBillingMessage(BillingMessage.ALREADY_HAVE_PURCHASE)
                retrofit.getPremiumInfo()
                return
            } else {
                if (plan == MainPremiumPlan.YEAR) {
                    billingManager.launchBillingFlow("prai_subscription_year")
                } else {
                    billingManager.launchBillingFlow("prai_subscription_month")
                }
                return
            }
        }
    }

    private suspend fun recoverBilling() {
        viewModel.billingMessage.value = BillingMessage.RECOVER_PROCESSING
        val result = retrofit.getPremiumInfoSuspend()
        if (result == null) {
            showBillingMessage(BillingMessage.PRAI_SERVER_ERROR)
            return
        }
        if (result.premium) {
            showBillingMessage(BillingMessage.ALREADY_PREMIUM)
            return
        } else {
            if (viewModel.billingItems.value.isEmpty()) {
                billingManager.initialize()
                showBillingMessage(BillingMessage.GOOGLE_PLAY_NO_ITEM)
                return
            }
            val purchases = billingManager.queryPurchasesSuspend()
            if (purchases.isNullOrEmpty()) {
                showBillingMessage(BillingMessage.RECOVER_NO_ITEM)
                return
            }
            if (purchases.any { it.purchaseState == Purchase.PurchaseState.PENDING } == true) {
                showBillingMessage(BillingMessage.PENDING)
                return
            }
            val resultList = mutableListOf<Event.PaymentEnrollResponse>()
            purchases.forEach { purchase ->
                val result = retrofit.enrollPaymentSuspend(
                    productId = purchase.products.getOrNull(0),
                    purchaseToken = purchase.purchaseToken
                )
                if (result != null) {
                    resultList.add(result)
                }
            }
            if (resultList.isNotEmpty()) {
                viewModel.isRecoverSuccessDialog.value = true
                return
            } else {
                showBillingMessage(BillingMessage.RECOVER_ALREADY_USED)
                return
            }
        }
    }

    private suspend fun showBillingMessage(message: BillingMessage) {
        viewModel.billingMessage.value = message
        delay(3000L)
        viewModel.billingMessage.value = null
    }

    private fun requestConversationList() {
        val userId = repository.userId
        if (userId != null) {
            retrofit.getConversationList(userId)
        } else {
            MainLogger.Activity.log("requestConversationList: userId is null")
        }
    }

    private fun startAddAccountActivity() {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        startActivity(intent)
    }

    private fun collectRecorderEvent() {
        MainScope().launch {
            recorder.event.collect { event ->
                when (event) {
                    is MainRecorder.Event.Success -> {
                        scope.launch { handleRecordSuccessEvent(event.path) }
                    }
                }
            }
        }
    }

    private fun collectBillingEvent() {
        MainScope().launch {
            billingManager.event.collect { event ->
                when (event) {
                    is MainBillingManager.Event.Connected -> {
                        // 연결 성공 시 제품 정보 조회
                        billingManager.queryAllProductDetails()
                        viewModel.billingState.value = MainBillingState.Connected
                    }

                    is MainBillingManager.Event.ProductDetailsLoaded -> {
                        viewModel.billingItems.value = event.productDetails.map { it.productId }
                        // 제품 정보 로드 완료
                        // updateProductUI(event.productDetails)
                    }

                    is MainBillingManager.Event.PurchaseSuccess -> {
                        // 구매 성공
                        // showPurchaseSuccessMessage()
                        withContext(Dispatchers.IO) {
                            val item = event.purchases.getOrNull(0) ?: return@withContext
                            val productId = item.products.getOrNull(0) ?: return@withContext
                            val result =
                                retrofit.enrollPaymentSuspend(productId, item.purchaseToken)
                            if (result == null || result.response.premium.not()) {
                                showBillingMessage(BillingMessage.BILLING_SUCCESS_BUT_PRAI_FAIL)
                            } else {
                                viewModel.billingMessage.value = null
                                viewModel.isBillingSuccessDialog.value = true
                            }
                        }
                    }

                    is MainBillingManager.Event.PurchasesLoaded -> {
                        // 구매 성공
                        // showPurchaseSuccessMessage()
                        withContext(Dispatchers.IO) {
                            event.purchases.forEach { item ->
                                val productId = item.products.getOrNull(0) ?: return@withContext
                                MainLogger.Billing.log("PurchasesLoaded: productId: $productId")
                                if (item.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                    val token = authManager.getAuthToken()
                                    if (token != null) {
                                        retrofit.enrollPayment(
                                            token,
                                            productId,
                                            item.purchaseToken
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is MainBillingManager.Event.Disconnected -> {
                        viewModel.billingState.value = MainBillingState.Disconnected
                    }

                    is MainBillingManager.Event.PurchaseCanceled -> {
                        viewModel.billingMessage.value = null
                    }

                    is MainBillingManager.Event.PurchaseError,
                    is MainBillingManager.Event.PurchasesError -> {
                        showBillingMessage(BillingMessage.GOOGLE_PLAY_PURCHASE_ERROR)
                    }

                    is MainBillingManager.Event.PurchasePending -> {
                        showBillingMessage(BillingMessage.PENDING)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private suspend fun handleRecordSuccessEvent(path: String) {
        val state = viewModel.callState.value
        if (state is MainCallState.Connected) {
            withContext(Dispatchers.Main) {
                viewModel.callResponseWaiting.value = true
            }
            val token = authManager.getAuthToken()
            if (token != null) {
                retrofit.sendCallRequest(
                    token,
                    path,
                    repository.selectedVoiceSettingItem.value.code,
                    repository.selectedVibeSettingItem.value.code,
                    repository.selectedVoiceSpeed.value,
                    state.conversationId
                )
            } else {
                viewModel.callResponseWaiting.value = false // handle Error case: token error
                viewModel.isServerErrorDialog.value = true
            }
            player.stop()
            Firebase.analytics.logEvent(
                "call_user_input_sent",
                bundleOf("conversation_id" to state.conversationId)
            )
        }
    }

    private suspend fun handleRecordStartEvent() {
        if (MainPermissionHandler.isGranted(this, MainPermission.AUDIO)) {
            if (viewModel.callState.value is MainCallState.Connected) {
                recorder.start(this)
                volumeReader.start(this)
                withContext(Dispatchers.Main) {
                    viewModel.startRecording()
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                viewModel.isRecordingPermissionDialog.value = true
            }
        }
    }

    private fun collectRetrofitEvent() {
        MainScope().launch {
            retrofit.event.collect { event ->
                handleRetrofitEvent(event)
                viewModel.onRetrofitEvent(event)
            }
        }
    }

    private suspend fun handleRetrofitEvent(event: MainRetrofit.Event) {
        when (event) {
            is MainRetrofit.Event.FirstCallResponse -> {
                if (viewModel.isFreeTrialCall.value == true) {
                    repository.saveFreeTrialTime()
                }
                if (viewModel.callState.value is MainCallState.Connecting) {
                    handleCallResponse(event.response.segments)
                    Firebase.analytics.logEvent(
                        "call_started",
                        bundleOf("conversation_id" to event.response.conversationId)
                    )
                }
            }

            is MainRetrofit.Event.CallResponse -> {
                if (viewModel.callState.value is MainCallState.Connected) {
                    if (viewModel.isFreeTrialCall.value.not() && event.response.premium.not()) {
                        viewModel.onCallEnd()
                        viewModel.isBillingVisible.value = true
                    } else {
                        handleCallResponse(event.response.segments)
                    }
                }
                viewModel.isPremiumUser.value = event.response.premium
                viewModel.premiumExpiresTime.value = event.response.expiresAt
            }

            is MainRetrofit.Event.MinVersionResponse -> {
                handleMinVersionResponse(event.response.meta.minVersion)
            }

            is MainRetrofit.Event.UserRegistrationResponse -> {
                withContext(Dispatchers.Main) {
                    Firebase.crashlytics.setUserId(event.response.user.userId)
                    Firebase.analytics.setUserId(event.response.user.userId)
                }
                retrofit.getPremiumInfo()
                delay(1000L)
                withContext(Dispatchers.Main) {
                    repository.updateServerUserInfo(event.response.user)
                    viewModel.isRegisterProcessing.value = false
                    viewModel.introState.value = MainIntroState.DONE
                }
            }

            is MainRetrofit.Event.UserRegistrationError -> {
                withContext(Dispatchers.Main) {
                    viewModel.isRegisterProcessing.value = false
                    viewModel.isServerErrorDialog.value = true
                }
            }

            is MainRetrofit.Event.PremiumInfoResponse -> {
                viewModel.isPremiumUser.value = event.response.premium
                viewModel.premiumExpiresTime.value = event.response.expiresAt
                viewModel.premiumChecked.value = true
            }

            is MainRetrofit.Event.PremiumInfoError -> {
                viewModel.premiumChecked.value = true
            }

            is MainRetrofit.Event.PaymentEnrollResponse -> {
                viewModel.premiumChecked.value = true
                viewModel.isPremiumUser.value = event.response.premium
                viewModel.premiumExpiresTime.value = event.response.expiresAt
            }


            else -> Unit
        }
    }

    private suspend fun handleCallResponse(segments: List<MainCallSegment>) {
        val items = segments.mapNotNull { segment ->
            val path = MainFileManager.createAudioFilePath(this)
            if (MainCodec.decodeBase64ToFile(segment.audio, path)) {
                CallSegmentItem(segment.text, path)
            } else {
                null
            }
        }
        withContext(Dispatchers.Main) {
            viewModel.updateSegmentItemList(items)
            viewModel.callResponseWaiting.value = false
        }
        player.start(items)
    }

    private suspend fun handleMinVersionResponse(serverVersion: String) {
        val myVersion = getAppVersion() ?: return

        val myMajor = myVersion.split('.')[0]
        val myMinor = myVersion.split('.')[1]

        val serverMajor = serverVersion.split('.')[0]
        val serverMinor = serverVersion.split('.')[1]

        if (myMajor < serverMajor || (myMajor == serverMajor && myMinor < serverMinor)) {
            withContext(Dispatchers.Main) {
                viewModel.forceUpdateDialog.value = true
            }
        }
    }

    private fun collectPlayerEvent() {
        MainScope().launch { player.event.collect { viewModel.onPlayerEvent(it) } }
    }

    private fun collectVolumeReaderEvent() {
        MainScope().launch { volumeReader.event.collect { viewModel.onVolumeEvent(it) } }
    }

    private fun getAppVersion(): String? {
        return try {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
            val version = packageInfo.versionName
            MainLogger.Activity.log("getAppVersion: $version")
            version
        } catch (exception: Exception) {
            MainLogger.Activity.log(exception, "getAppVersion: exception: $exception")
            null
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 2404)?.show()
            } else {
                MainLogger.Activity.log("This device is not supported for Google Play services.")
            }
            return false
        }
        return true
    }

    private fun collectRepositoryEvent() {
        MainScope().launch {
            repository.event.collect { event ->
                if (event is MainRepositoryViewModel.Event.SaveUserInfo) {
                    scope.launch {
                        val token = authManager.getAuthToken() ?: return@launch
                        retrofit.updateUserInfo(
                            authToken = token,
                            name = event.name,
                            gender = event.gender.code,
                            email = repository.email,
                            birthYear = event.age
                        )
                    }
                }
            }
        }
    }
}