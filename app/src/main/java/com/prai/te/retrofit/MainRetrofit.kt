package com.prai.te.retrofit

import com.prai.te.auth.MainAuthManager
import com.prai.te.common.MainCodec
import com.prai.te.common.MainLogger
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class MainRetrofit(private val scope: CoroutineScope, val authManager: MainAuthManager) {
    val event: SharedFlow<Event> by lazy { mutableEvent.asSharedFlow() }

    private val service: MainApiService by lazy { createService() }
    private val mutableEvent = MutableSharedFlow<Event>()

    fun sendFirstCallRequest(
        authToken: String,
        userId: String,
        voice: String,
        vibe: String,
        speed: Float
    ) {
        val request = MainFirstCallRequest(
            userId = userId,
            voice = voice,
            ttsOption = createTtsOption(vibe, speed)
        )
        MainLogger.Retrofit.log("sendFirstCallRequest: request: $request")
        scope.launch {
            try {
                val response = service.sendFirstCallRequest("Bearer $authToken", request)
                mutableEvent.emit(Event.FirstCallResponse(response))
                MainLogger.Retrofit.log("sendFirstCallRequest: success: $response")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log(exception, "sendFirstCallRequest: exception: $exception")
                mutableEvent.emit(Event.CallResponseError)
            }
        }
    }

    fun sendCallRequest(
        token: String,
        path: String,
        voice: String,
        vibe: String,
        speed: Float,
        id: String
    ) {
        val encoded = MainCodec.encodeFilePathToBase64(path)
        val request = MainCallRequest(
            audio = encoded,
            voice = voice,
            ttsOption = createTtsOption(vibe, speed),
            conversationId = id
        )
        MainLogger.Retrofit.log("sendCallRequest: request: $request")
        scope.launch {
            try {
                val response = service.sendCallRequest("Bearer $token", request)
                mutableEvent.emit(Event.CallResponse(response))
                MainLogger.Retrofit.log("sendCallRequest: token: $token")
                MainLogger.Retrofit.log("sendCallRequest: success: $response")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.CallResponseError)
                MainLogger.Retrofit.log(exception, "sendCallRequest: exception: $exception")
            }
        }
    }

    fun getConversationList(userId: String, limit: Int = 30, cursor: String? = null) {
        MainLogger.Retrofit.log("getConversationList: userId: $userId, limit: $limit, cursor: $cursor")
        scope.launch {
            try {
                val response = service.getConversationList(userId, limit, cursor)
                mutableEvent.emit(Event.ConversationListResponse(response))
                MainLogger.Retrofit.log("getConversationList, success: $response")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log(exception, "getConversationList, exception: $exception")
            }
        }
    }

    fun getConversation(conversationId: String, limit: Int = 50, cursor: String? = null) {
        MainLogger.Retrofit.log("getConversation: conversationId: $conversationId, limit: $limit, cursor: $cursor")
        scope.launch {
            try {
                val response = service.getConversation(conversationId, limit, cursor)
                mutableEvent.emit(Event.ConversationResponse(response))
                MainLogger.Retrofit.log("getConversation, success: $response")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log(exception, "getConversation, exception: $exception")
            }
        }
    }

    fun getTranslation(text: String, targetLanguage: String = "Korean") {
        val request = MainTranslationRequest(text, targetLanguage)
        MainLogger.Retrofit.log("getTranslation: request: $request")
        scope.launch {
            try {
                val response = service.translateText(request)
                mutableEvent.emit(Event.TranslationResponse(text, response))
                MainLogger.Retrofit.log("getTranslation: success: ${response.translation}")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log(exception, "getTranslation: exception: $exception")
            }
        }
    }

    fun registerUser(
        authToken: String,
        name: String,
        birthYear: String? = null,
        gender: String? = null
    ) {
        val request = MainUserRegistrationRequest(name, birthYear, gender)
        MainLogger.Retrofit.log("registerUser: request: $request")

        scope.launch {
            try {
                val response = service.registerUser("Bearer $authToken", request)
                mutableEvent.emit(Event.UserRegistrationResponse(response))
                MainLogger.Retrofit.log("registerUser: success: $response")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.UserRegistrationError)
                MainLogger.Retrofit.log(exception, "registerUser: exception: $exception")
            }
        }
    }

//    fun getUserInfo(authToken: String) {
//        scope.launch {
//            try {
//                val response = service.getUserInfo("Bearer $authToken")
//                mutableEvent.emit(Event.UserInfoResponse(response))
//                MainLogger.Retrofit.log("getUserInfo: success: $response")
//            } catch (exception: HttpException) {
//                if (exception.code() == 404) {
//                    mutableEvent.emit(Event.UserInfoNotFound)
//                    MainLogger.Retrofit.log("getUserInfo: not found")
//                } else {
//                    mutableEvent.emit(Event.UserApiError(exception.message ?: "Unknown error"))
//                    MainLogger.Retrofit.log("getUserInfo: exception: $exception")
//                }
//            } catch (exception: Exception) {
//                mutableEvent.emit(Event.UserApiError(exception.message ?: "Unknown error"))
//                MainLogger.Retrofit.log("getUserInfo: exception: $exception")
//            }
//        }
//    }

    suspend fun getUserInfoBlocking(
        authToken: String
    ): Event = suspendCancellableCoroutine { continuation ->
        MainLogger.Retrofit.log("getUserInfoBlocking: authToken: ${authToken.take(10)}...")
        scope.launch {
            try {
                val response = service.getUserInfo("Bearer $authToken")
                continuation.resume(Event.UserInfoResponse(response))
                MainLogger.Retrofit.log("getUserInfo: success: $response")
            } catch (exception: HttpException) {
                if (exception.code() == 404) {
                    continuation.resume(Event.UserInfoNotFound)
                    MainLogger.Retrofit.log("getUserInfo: not found")
                } else {
                    continuation.resume(Event.UserApiError(exception.message ?: "Unknown error"))
                    MainLogger.Retrofit.log(exception, "getUserInfo: exception: $exception")
                }
            } catch (exception: Exception) {
                continuation.resume(Event.UserApiError(exception.message ?: "Unknown error"))
                MainLogger.Retrofit.log(exception, "getUserInfo: exception: $exception")
            }
        }
    }

    fun updateUserInfo(
        authToken: String,
        name: String,
        email: String? = null,
        birthYear: String? = null,
        gender: String? = null
    ) {
        val request = MainUserUpdateRequest(name, email, birthYear, gender)
        MainLogger.Retrofit.log("updateUserInfo: request: $request")

        scope.launch {
            try {
                val response = service.updateUserInfo("Bearer $authToken", request)
                mutableEvent.emit(Event.UserUpdateResponse(response))
                MainLogger.Retrofit.log("updateUserInfo: success: $response")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.UserApiError(exception.message ?: "Unknown error"))
                MainLogger.Retrofit.log(exception, "updateUserInfo: exception: $exception")
            }
        }
    }

    fun deleteUser(authToken: String) {
        MainLogger.Retrofit.log("deleteUser: authToken: ${authToken.take(10)}...")

        scope.launch {
            try {
                val response = service.deleteUser("Bearer $authToken")
                mutableEvent.emit(Event.UserDeletionResponse(response))
                MainLogger.Retrofit.log("deleteUser: success: $response")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.UserApiError(exception.message ?: "Unknown error"))
                MainLogger.Retrofit.log(exception, "deleteUser: exception: $exception")
            }
        }
    }

    fun getMinimumVersion(clientType: String = "android") {
        MainLogger.Retrofit.log("getMinimumVersion: clientType: $clientType")
        scope.launch {
            try {
                val response = service.getMinimumVersion(clientType)
                mutableEvent.emit(Event.MinVersionResponse(response))
                MainLogger.Retrofit.log("getMinimumVersion: success: $response")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.VersionApiError(exception.message ?: "Unknown error"))
                MainLogger.Retrofit.log(exception, "getMinimumVersion: exception: $exception")
            }
        }
    }

    fun enrollPayment(
        authToken: String,
        productId: String? = null,
        purchaseToken: String? = null
    ) {
        val request = MainPaymentEnrollRequest(
            platform = "android",
            productId = productId,
            purchaseToken = purchaseToken
        )
        MainLogger.Retrofit.log("enrollPayment: request: $request")

        scope.launch {
            try {
                val response = service.enrollPayment("Bearer $authToken", request)
                mutableEvent.emit(Event.PaymentEnrollResponse(response))
                MainLogger.Retrofit.log("enrollPayment: success: $response")
            } catch (exception: HttpException) {
                val errorCode = exception.code()
                val errorMessage = exception.message() ?: "Unknown error"
                mutableEvent.emit(Event.PaymentApiError(errorCode, errorMessage))
                MainLogger.Retrofit.log(
                    exception,
                    "enrollPayment: HttpException: code=$errorCode, message=$errorMessage"
                )
            } catch (exception: Exception) {
                mutableEvent.emit(Event.PaymentApiError(500, exception.message ?: "Unknown error"))
                MainLogger.Retrofit.log(exception, "enrollPayment: exception: $exception")
            }
        }
    }

    suspend fun enrollPaymentSuspend(
        productId: String? = null,
        purchaseToken: String? = null
    ): Event.PaymentEnrollResponse? {
        val request = MainPaymentEnrollRequest(
            platform = "android",
            productId = productId,
            purchaseToken = purchaseToken
        )
        MainLogger.Retrofit.log("enrollPaymentSuspend: request: $request")
        try {
            val authToken = authManager.getAuthToken() ?: throw SecurityException()
            val response = service.enrollPayment("Bearer $authToken", request)
            mutableEvent.emit(Event.PaymentEnrollResponse(response))
            MainLogger.Retrofit.log("enrollPaymentSuspend: success: $response")
            return Event.PaymentEnrollResponse(response)
        } catch (exception: HttpException) {
            val errorCode = exception.code()
            val errorMessage = exception.message() ?: "Unknown error"
            mutableEvent.emit(Event.PaymentApiError(errorCode, errorMessage))
            MainLogger.Retrofit.log(
                exception,
                "enrollPaymentSuspend: HttpException: code=$errorCode, message=$errorMessage"
            )
            return null
        } catch (exception: Exception) {
            mutableEvent.emit(Event.PaymentApiError(500, exception.message ?: "Unknown error"))
            MainLogger.Retrofit.log(exception, "enrollPaymentSuspend: exception: $exception")
            return null
        }
    }

    fun getPremiumInfo() {
        scope.launch {
            try {
                val token = authManager.getAuthToken() ?: throw SecurityException()
                val response = service.getPremiumInfo("Bearer $token")
                mutableEvent.emit(Event.PremiumInfoResponse(response))
                MainLogger.Retrofit.log("getPremiumInfo: success: $response")
            } catch (exception: HttpException) {
                val errorMessage = exception.message ?: "Unknown error"
                mutableEvent.emit(Event.PremiumInfoError(errorMessage))
                MainLogger.Retrofit.log(exception, "getPremiumInfo: HttpException: $errorMessage")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.PremiumInfoError(exception.message ?: "Unknown error"))
                MainLogger.Retrofit.log(exception, "getPremiumInfo: exception: $exception")
            }
        }
    }

    suspend fun getPremiumInfoSuspend(): MainPremiumInfoResponse? {
        try {
            val token = authManager.getAuthToken() ?: throw SecurityException()
            val response = service.getPremiumInfo("Bearer $token")
            mutableEvent.emit(Event.PremiumInfoResponse(response))
            MainLogger.Retrofit.log("getPremiumInfo: success: $response")
            return response
        } catch (exception: HttpException) {
            val errorMessage = exception.message ?: "Unknown error"
            mutableEvent.emit(Event.PremiumInfoError(errorMessage))
            MainLogger.Retrofit.log(exception, "getPremiumInfo: HttpException: $errorMessage")
            return null
        } catch (exception: Exception) {
            mutableEvent.emit(Event.PremiumInfoError(exception.message ?: "Unknown error"))
            MainLogger.Retrofit.log(exception, "getPremiumInfo: exception: $exception")
            return null
        }
    }

    fun sendCancellationReason(reason: String) {
        val request = MainCancellationReasonRequest("android", reason)
        MainLogger.Retrofit.log("sendCancellationReason: request: $request")

        scope.launch {
            try {
                val token = authManager.getAuthToken() ?: throw SecurityException()
                val response = service.sendCancellationReason("Bearer $token", request)
                mutableEvent.emit(Event.CancellationReasonResponse(response))
                MainLogger.Retrofit.log("sendCancellationReason: success: $response")
            } catch (exception: HttpException) {
                val errorCode = exception.code()
                val errorMessage = exception.message() ?: "Unknown error"
                mutableEvent.emit(Event.CancellationReasonError(errorCode, errorMessage))
                MainLogger.Retrofit.log(
                    exception,
                    "sendCancellationReason: HttpException: code=$errorCode, message=$errorMessage"
                )
            } catch (exception: Exception) {
                mutableEvent.emit(
                    Event.CancellationReasonError(
                        500,
                        exception.message ?: "Unknown error"
                    )
                )
                MainLogger.Retrofit.log(exception, "sendCancellationReason: exception: $exception")
            }
        }
    }

    private fun createService(): MainApiService {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MainApiService::class.java)
    }

    private fun createTtsOption(vibe: String, speed: Float): String {
        val minSpeed = 10f
        val maxSpeed = 200f
        val speedPercent = minSpeed + (maxSpeed - minSpeed) * speed
        return "Speak at ${speedPercent}% of normal speed. Use a $vibe Accent."
    }


    sealed interface Event {
        data class CallResponse(val response: MainCallResponse) : Event
        data object CallResponseError : Event

        data class FirstCallResponse(val response: MainFirstCallResponse) : Event
        data class ConversationListResponse(val response: MainConversationListResponse) : Event
        data class ConversationResponse(val response: MainConversationResponse) : Event
        data class TranslationResponse(
            val originalText: String,
            val response: MainTranslationResponse
        ) : Event

        data class UserRegistrationResponse(val response: MainUserRegistrationResponse) : Event
        data object UserRegistrationError : Event
        data class UserInfoResponse(val response: MainUserInfo) : Event
        data class UserUpdateResponse(val response: MainUserUpdateResponse) : Event
        data class UserDeletionResponse(val response: MainUserDeletionResponse) : Event
        data class UserApiError(val errorMessage: String) : Event
        data object UserInfoNotFound : Event
        data class MinVersionResponse(val response: MainMinVersionResponse) : Event
        data class VersionApiError(val errorMessage: String) : Event
        data class PaymentEnrollResponse(val response: MainPaymentEnrollResponse) : Event
        data class PaymentApiError(val errorCode: Int, val errorMessage: String) : Event
        data class PremiumInfoResponse(val response: MainPremiumInfoResponse) : Event
        data class PremiumInfoError(val errorMessage: String) : Event

        data class CancellationReasonResponse(val response: MainCancellationReasonResponse) : Event
        data class CancellationReasonError(val errorCode: Int, val errorMessage: String) : Event

    }

    companion object {
        private const val BASE_URL = "https://x1nzi9gkn2.execute-api.ap-northeast-2.amazonaws.com/"
    }
}