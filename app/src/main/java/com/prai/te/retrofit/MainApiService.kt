package com.prai.te.retrofit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface MainApiService {
    @POST("call")
    @Headers("Content-Type: application/json")
    suspend fun sendCallRequest(@Body request: MainCallRequest): MainCallResponse

    @POST("call")
    @Headers("Content-Type: application/json")
    suspend fun sendFirstCallRequest(@Body request: MainFirstCallRequest): MainFirstCallResponse

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
}

internal data class MainFirstCallRequest(
    val userId: String = "test",
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
    val segments: List<MainCallSegment>,
    val processingTimes: MainCallSegment.ProcessingTimes
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