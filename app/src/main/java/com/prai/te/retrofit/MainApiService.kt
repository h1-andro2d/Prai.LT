package com.prai.te.retrofit

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface MainApiService {
    @POST("v2/call")
    @Headers("Content-Type: application/json")
    suspend fun sendCallRequest(
        @Header("Authorization") authToken: String,
        @Body request: MainCallRequest
    ): MainCallResponse

    @POST("v2/call")
    @Headers("Content-Type: application/json")
    suspend fun sendFirstCallRequest(
        @Header("Authorization") authToken: String,
        @Body request: MainFirstCallRequest
    ): MainFirstCallResponse

    @GET("/conversations")
    suspend fun getConversationList(
        @Query("userId") userId: String,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): MainConversationListResponse

    @GET("/conversations/{conversationId}/messages")
    suspend fun getConversation(
        @Path("conversationId") conversationId: String,
        @Query("limit") limit: Int = 50,
        @Query("cursor") cursor: String? = null
    ): MainConversationResponse

    @POST("translation")
    suspend fun translateText(@Body request: MainTranslationRequest): MainTranslationResponse

    @POST("users")
    @Headers("Content-Type: application/json")
    suspend fun registerUser(
        @Header("Authorization") authToken: String,
        @Body request: MainUserRegistrationRequest
    ): MainUserRegistrationResponse

    @GET("users")
    suspend fun getUserInfo(
        @Header("Authorization") authToken: String
    ): MainUserInfo

    @PUT("users")
    @Headers("Content-Type: application/json")
    suspend fun updateUserInfo(
        @Header("Authorization") authToken: String,
        @Body request: MainUserUpdateRequest
    ): MainUserUpdateResponse

    @DELETE("users")
    suspend fun deleteUser(
        @Header("Authorization") authToken: String
    ): MainUserDeletionResponse

    @GET("version/min")
    suspend fun getMinimumVersion(
        @Query("client") clientType: String
    ): MainMinVersionResponse
    @POST("payments/enroll")

    suspend fun enrollPayment(
        @Header("Authorization") authToken: String,
        @Body request: MainPaymentEnrollRequest
    ): MainPaymentEnrollResponse

    @GET("payments/info")
    suspend fun getPremiumInfo(
        @Header("Authorization") authToken: String
    ): MainPremiumInfoResponse

    @POST("payments/cancel")
    @Headers("Content-Type: application/json")
    suspend fun sendCancellationReason(
        @Header("Authorization") authToken: String,
        @Body request: MainCancellationReasonRequest
    ): MainCancellationReasonResponse
}

internal data class MainFirstCallRequest(
    val userId: String,
    val voice: String? = null,
    val ttsOption: String? = null
)

internal data class MainFirstCallResponse(
    val aiText: String,
    val userId: String,
    val conversationId: String,
    val segments: List<MainCallSegment>,
    val isNewCall: Boolean,
    val processingTimes: MainCallSegment.ProcessingTimes
)

internal data class MainCallRequest(
    val audio: String,
    val format: String = "m4a",
    val voice: String? = null,
    val ttsOption: String? = null,
    val conversationId: String? = null
)

internal data class MainCallResponse(
    val userText: String,
    val aiText: String,
    val userId: String,
    val conversationId: String,
    val premium: Boolean,
    val expiresAt: String?,
    val processingTimes: MainCallSegment.ProcessingTimes,
    val segments: List<MainCallSegment>
)

internal data class MainCallSegment(
    val index: Int,
    val text: String,
    val audio: String,
    val processingTime: Float
) {
    data class ProcessingTimes(
        val stt: Float = 0f,
        val gpt: Float = 0f,
        val tts: Float = 0f,
        val total: Float = 0f
    )
}

internal data class MainConversationListResponse(
    val conversations: List<MainConversationMeta>,
    val total: Int,
    val nextCursor: String?
)

internal data class MainConversationMeta(
    val id: String,
    val createdAt: String,
    val lastMessage: String?,
    val duration: Int
)

internal data class MainConversationResponse(
    val conversationId: String,
    val messages: List<MainConversation>,
    val total: Int,
    val nextCursor: String?
)

internal data class MainConversation(
    val id: String,
    val timestamp: String,
    val speaker: String,
    val text: String,
    val userId: String
)

internal data class MainTranslationRequest(
    val text: String,
    val targetLanguage: String = "Korean"
)

internal data class MainTranslationResponse(
    val translation: String
)

internal data class MainUserRegistrationRequest(
    val name: String,
    val birthYear: String? = null,
    val gender: String? = null
)

internal data class MainUserRegistrationResponse(
    val message: String,
    val user: MainUserInfo
)

internal data class MainUserInfo(
    val userId: String,
    val name: String,
    val email: String?,
    val birthYear: String?,
    val gender: String?,
    val createdAt: String
)

internal data class MainUserUpdateRequest(
    val name: String,
    val email: String? = null,
    val birthYear: String? = null,
    val gender: String? = null
)

internal data class MainUserUpdateResponse(
    val message: String,
    val user: MainUserInfo
)

internal data class MainUserDeletionResponse(
    val message: String,
    val deletedUser: MainDeletedUserInfo
)

internal data class MainDeletedUserInfo(
    val userId: String,
    val deletedAt: String,
    val retentionUntil: String,
    val name: String,
    val email: String?,
    val gender: String?,
    val birthYear: String?,
    val conversations: List<MainDeletedConversationInfo>?
)

internal data class MainDeletedConversationInfo(
    val conversationId: String,
    val createdAt: String,
    val messages: List<Any>? // TODO
)

internal data class MainMinVersionResponse(
    val meta: MainMinVersionMeta
)

internal data class MainMinVersionMeta(
    val minVersion: String
)

data class MainPaymentEnrollRequest(
    val platform: String,
    val productId: String? = null,
    val purchaseToken: String? = null,
    val receiptData: String? = null
)

data class MainPaymentEnrollResponse(
    val userId: String,
    val premium: Boolean,
    val expiresAt: String,
    val platform: String
)

internal data class MainPremiumInfoResponse(
    val userId: String,
    val premium: Boolean,
    val expiresAt: String? // 유효한 구독 없을 경우 null
)

internal data class MainCancellationReasonRequest(
    val platform: String,
    val reason: String
)

internal data class MainCancellationReasonResponse(
    val message: String
)
