package com.prai.te.billing

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.prai.te.common.MainLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import com.prai.te.retrofit.MainRetrofit

internal class MainBillingManager(
    private val activity: Activity,
    private val retrofit: MainRetrofit
) {
    val event by lazy { mutableEvent.asSharedFlow() }

    private val mutableEvent = MutableSharedFlow<Event>()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val subscriptionProductIds = listOf("prai_subscription_year", "prai_subscription_month")
    private val cachedProductDetails = mutableMapOf<String, ProductDetails>()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                if (purchases != null) {
                    scope.launch {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }
                    dispatchEvent(Event.PurchaseSuccess(purchases)) // 여기서. 최초 결제 확인 가능 or ACK 시점에서.
                }
            }

            BillingResponseCode.USER_CANCELED -> {
                dispatchEvent(Event.PurchaseCanceled)
            }

            else -> {
                dispatchEvent(
                    Event.PurchaseError(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    private var billingClient = BillingClient.newBuilder(activity)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams
                .newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    fun initialize() {
        if (billingClient.isReady) {
            dispatchEvent(Event.Connected)
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                scope.launch {
                    delay(1000L)
                    initialize()
                }
                dispatchEvent(Event.Disconnected)
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingResponseCode.OK) {
                    dispatchEvent(Event.Connected)
                    scope.launch { queryPurchases() }
                } else {
                    MainLogger.Billing.log("onBillingSetupFinished: ${result.responseCode}")
                    dispatchEvent(Event.ConnectionFailed(result.responseCode, result.debugMessage))
                }
            }
        })
    }

    fun queryAllProductDetails() {
        if (billingClient.isReady.not()) {
            dispatchEvent(Event.NotConnected)
            return
        }

        scope.launch {
            val detailsList = queryProductDetails(subscriptionProductIds, ProductType.SUBS)
            detailsList.forEach { details ->
                cachedProductDetails[details.productId] = details
            }
            dispatchEvent(Event.ProductDetailsLoaded(detailsList))
        }
    }

    private fun queryProductDetails(productId: String) {
        if (!billingClient.isReady) {
            dispatchEvent(Event.NotConnected)
            return
        }

        scope.launch {
            val details = queryProductDetails(listOf(productId), ProductType.SUBS)
            if (details.isNotEmpty()) {
                cachedProductDetails[productId] = details[0]
                dispatchEvent(Event.ProductDetailsLoaded(details))
            } else {
                dispatchEvent(Event.ProductDetailsError("제품 정보를 찾을 수 없습니다: $productId"))
            }
        }
    }

    /**
     * 제품 ID 목록에 대한 제품 정보를 조회합니다.
     */
    private suspend fun queryProductDetails(
        productIds: List<String>,
        productType: String
    ): List<ProductDetails> {
        if (productIds.isEmpty()) return emptyList()

        val productList = productIds.map { productId ->
            Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val result = billingClient.queryProductDetails(params)
                if (result.billingResult.responseCode == BillingResponseCode.OK) {
                    result.productDetailsList ?: emptyList()
                } else {
                    MainLogger.Billing.log("queryProductDetails error: ${result.billingResult.responseCode}")
                    emptyList()
                }
            } catch (e: Exception) {
                MainLogger.Billing.log(e, "queryProductDetails exception: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun queryPurchasesSuspend(): List<Purchase>? {
        if (billingClient.isReady.not()) {
            return null
        }
        try {
            val subsResult = billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build()
            )
            return subsResult.purchasesList
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun queryPurchases() {
        if (billingClient.isReady.not()) {
            dispatchEvent(Event.NotConnected)
            return
        }

        try {
            val subsResult = billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build()
            )

            for (purchase in subsResult.purchasesList) {
                handlePurchase(purchase)
            }

            dispatchEvent(Event.PurchasesLoaded(subsResult.purchasesList))
        } catch (e: Exception) {
            MainLogger.Billing.log(e, "queryPurchases exception: ${e.message}")
            dispatchEvent(Event.PurchasesError("구매 정보 조회 중 오류 발생: ${e.message}"))
        }
    }

    fun launchBillingFlow(productId: String) {
        val productDetails = cachedProductDetails[productId]
        if (productDetails != null) {
            launchBillingFlow(productDetails)
        } else {
            queryProductDetails(productId)
            dispatchEvent(Event.ProductDetailsNotFound(productId))
        }
    }

    private fun launchBillingFlow(detail: ProductDetails) {
        if (billingClient.isReady.not()) {
            dispatchEvent(Event.NotConnected)
            return
        }

        try {
            val productDetailsParamsList = if (detail.productType == ProductType.SUBS) {
                // 구독 상품인 경우 첫 번째 제안 사용
                val offerToken = detail.subscriptionOfferDetails?.firstOrNull()?.offerToken
                if (offerToken == null) {
                    dispatchEvent(Event.PurchaseError(0, "구독 제안을 찾을 수 없습니다"))
                    return
                }

                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(detail)
                        .setOfferToken(offerToken)
                        .build()
                )
            } else {
                // 일회성 상품인 경우
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(detail)
                        .build()
                )
            }

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

            if (billingResult.responseCode != BillingResponseCode.OK) {
                dispatchEvent(
                    Event.PurchaseError(
                        billingResult.responseCode,
                        billingResult.debugMessage
                    )
                )
            }
        } catch (e: Exception) {
            MainLogger.Billing.log(e, "launchBillingFlow exception: ${e.message}")
            dispatchEvent(Event.PurchaseError(0, "구매 시작 중 오류 발생: ${e.message}"))
        }
    }

    /**
     * 연결을 종료합니다.
     */
    fun endConnection() {
        billingClient.endConnection()
    }

    /**
     * 이벤트를 발생시킵니다.
     */
    private fun dispatchEvent(event: Event) {
        MainLogger.Billing.log("dispatchEvent: $event")
        scope.launch {
            mutableEvent.emit(event)
        }
    }

    private fun handlePendingPurchase(purchase: Purchase) {
        // 대기 중인 구매 정보 저장
        savePendingPurchase(purchase)

        // 사용자에게 대기 상태 알림
        dispatchEvent(Event.PurchasePending(purchase))
    }

    /**
     * 대기 중인 구매 정보를 저장합니다.
     */
    private fun savePendingPurchase(purchase: Purchase) {
        // SharedPreferences나 데이터베이스에 대기 중인 구매 정보 저장
        // 이 정보는 앱이 다시 시작될 때 확인하는 데 사용됩니다.
        val pendingPurchases = getPendingPurchases() + purchase.purchaseToken
        savePendingPurchaseTokens(pendingPurchases)
    }

    /**
     * 저장된 대기 중인 구매 목록을 가져옵니다.
     */
    private fun getPendingPurchases(): List<String> {
        // SharedPreferences나 데이터베이스에서 대기 중인 구매 토큰 목록 조회
        val sharedPrefs = activity.getSharedPreferences("billing_prefs", Activity.MODE_PRIVATE)
        val pendingPurchasesJson = sharedPrefs.getString("pending_purchases", "[]")
        return try {
            // JSON 문자열을 List<String>으로 변환
            // 실제 구현에서는 적절한 JSON 라이브러리 사용
            pendingPurchasesJson?.let {
                if (it == "[]") emptyList() else it.substring(1, it.length - 1).split(",")
            } ?: emptyList()
        } catch (e: Exception) {
            MainLogger.Billing.log(e, "getPendingPurchases error: ${e.message}")
            emptyList()
        }
    }

    /**
     * 대기 중인 구매 목록을 저장합니다.
     */
    private fun savePendingPurchaseTokens(tokens: List<String>) {
        // SharedPreferences나 데이터베이스에 대기 중인 구매 토큰 목록 저장
        val sharedPrefs = activity.getSharedPreferences("billing_prefs", Activity.MODE_PRIVATE)
        val pendingPurchasesJson = tokens.toString()
        sharedPrefs.edit { putString("pending_purchases", pendingPurchasesJson) }
    }

    /**
     * 구매를 처리합니다.
     */
    private suspend fun handlePurchase(purchase: Purchase) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                // 구매 완료 처리
                if (!purchase.isAcknowledged) {
//                    acknowledgePurchase(purchase)
                }

                // 대기 중인 구매 목록에서 제거
                removePendingPurchase(purchase.purchaseToken)

                // 구매 성공 이벤트 발생
                dispatchEvent(Event.PurchaseProcessed(purchase))
            }

            Purchase.PurchaseState.PENDING -> {
                // 대기 중인 구매 처리
                handlePendingPurchase(purchase)
            }

            else -> {
                // 기타 상태 처리
                MainLogger.Billing.log("Unknown purchase state: ${purchase.purchaseState}")
            }
        }
    }

    /**
     * 대기 중인 구매 목록에서 구매 토큰을 제거합니다.
     */
    private fun removePendingPurchase(purchaseToken: String) {
        val pendingPurchases = getPendingPurchases().filter { it != purchaseToken }
        savePendingPurchaseTokens(pendingPurchases)
    }

    /**
     * 구매를 확인합니다.
     */
    private suspend fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        try {
            val result = billingClient.acknowledgePurchase(acknowledgePurchaseParams)
            if (result.responseCode == BillingResponseCode.OK) {
                dispatchEvent(Event.PurchaseAcknowledged(purchase))
            } else {
                MainLogger.Billing.log("acknowledgePurchase error: ${result.responseCode}")
                dispatchEvent(
                    Event.PurchaseError(
                        result.responseCode,
                        "구매 확인 실패: ${result.debugMessage}"
                    )
                )
            }
        } catch (e: Exception) {
            MainLogger.Billing.log(e, "acknowledgePurchase exception: ${e.message}")
            dispatchEvent(Event.PurchaseError(0, "구매 확인 중 오류 발생: ${e.message}"))
        }
    }

    /**
     * 결제 관련 이벤트
     */
    sealed interface Event {
        // 연결 관련 이벤트
        data object Connected : Event
        data object Disconnected : Event
        data object NotConnected : Event
        data class ConnectionFailed(val responseCode: Int, val message: String) : Event

        // 제품 정보 관련 이벤트
        data class ProductDetailsLoaded(val productDetails: List<ProductDetails>) : Event
        data class ProductDetailsError(val message: String) : Event
        data class ProductDetailsNotFound(val productId: String) : Event

        // 구매 관련 이벤트
        data class PurchaseSuccess(val purchases: List<Purchase>) : Event
        data object PurchaseCanceled : Event
        data class PurchaseError(val responseCode: Int, val message: String) : Event
        data class PurchasesLoaded(val purchases: List<Purchase>) : Event
        data class PurchasesError(val message: String) : Event
        data class PurchaseProcessed(val purchase: Purchase) : Event
        data class PurchasePending(val purchase: Purchase) : Event
        data class PurchaseAcknowledged(val purchase: Purchase) : Event
    }
}
