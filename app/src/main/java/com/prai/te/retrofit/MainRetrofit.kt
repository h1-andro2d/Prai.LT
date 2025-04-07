package com.prai.te.retrofit

import com.prai.te.common.MainCodec
import com.prai.te.common.MainLogger
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class MainRetrofit(coroutineContext: CoroutineContext) {
    val event: SharedFlow<Event> by lazy { mutableEvent.asSharedFlow() }

    private val scope = CoroutineScope(Dispatchers.IO + coroutineContext)
    private val service: MainApiService by lazy { createService() }
    private val mutableEvent = MutableSharedFlow<Event>()

    fun sendFirstCallRequest(voice: String, vibe: String, speed: Float) {
        val request = MainFirstCallRequest(
            voice = voice,
            ttsOption = createTtsOption(vibe, speed)
        )
        scope.launch {
            try {
                val response = service.sendFirstCallRequest(request)
                mutableEvent.emit(Event.FirstCallResponse(response))
                MainLogger.Retrofit.log("sendFirstCallRequest: success: $response")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log("sendFirstCallRequest: exception: $exception")
            }
        }
    }

    fun sendCallRequest(
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
        scope.launch {
            try {
                val response = service.sendCallRequest(request)
                mutableEvent.emit(Event.CallResponse(response))
                MainLogger.Retrofit.log("sendCallRequest: success: $response")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.CallResponseError)
                MainLogger.Retrofit.log("sendCallRequest: exception: $exception")
            }
        }
    }

    fun getConversationList(userId: String = "test", limit: Int = 20, cursor: String? = null) {
        scope.launch {
            try {
                val response = service.getConversationList(userId, limit, cursor)
                mutableEvent.emit(Event.ConversationListResponse(response))
                MainLogger.Retrofit.log("getConversationList, success: $response")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log("getConversationList, exception: $exception")
            }
        }
    }

    fun getConversation(conversationId: String, limit: Int = 50, cursor: String? = null) {
        scope.launch {
            try {
                val response = service.getConversation(conversationId, limit, cursor)
                mutableEvent.emit(Event.ConversationResponse(response))
                MainLogger.Retrofit.log("getConversation, success: $response")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log("getConversation, exception: $exception")
            }
        }
    }

    fun getTranslation(text: String, targetLanguage: String = "Korean") {
        val request = MainTranslationRequest(text, targetLanguage)
        scope.launch {
            try {
                val response = service.translateText(request)
                mutableEvent.emit(Event.TranslationResponse(text, response))
                MainLogger.Retrofit.log("getTranslation: success: ${response.translation}")
            } catch (exception: Exception) {
                MainLogger.Retrofit.log("getTranslation: exception: $exception")
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
    }

    companion object {
        private const val BASE_URL = "https://x1nzi9gkn2.execute-api.ap-northeast-2.amazonaws.com/"
    }
}