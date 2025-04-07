package com.prai.te.media

import android.content.Context
import android.media.MediaRecorder
import com.prai.te.common.MainLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class MainRecorder(private val scope: CoroutineScope) {
    val event: SharedFlow<Event> by lazy { mutableEvent.asSharedFlow() }

    private val mutableEvent = MutableSharedFlow<Event>()
    private var recorder: MediaRecorder? = null
        set(value) {
            if (field != value) {
                field?.tryRelease()
                field = value
                value?.safeStart()
            }
        }
    private var filePath: String? = null

    fun start(context: Context) {
        val path = MainFileManager.createAudioFilePath(context)
        recorder = createRecorder(context, path)
        if (recorder != null) {
            filePath = path
        }
        MainLogger.Recorder.log("start")
    }

    fun stop() {
        recorder = null
        tryEmitSuccessEvent()
        MainLogger.Recorder.log("stop")
    }

    fun cancel() {
        recorder = null
        MainLogger.Recorder.log("stop")
    }

    private fun tryEmitSuccessEvent() {
        val path = filePath ?: return
        scope.launch { mutableEvent.emit(Event.Success(path)) }
        filePath = null
    }

    private fun createRecorder(context: Context, path: String): MediaRecorder? {
        val recorder = MediaRecorder(context)

        try {
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(path)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(32000)
                prepare()
            }
        } catch (exception: Exception) {
            MainLogger.Recorder.log("error: createRecorder, exception: $exception")
            return null
        }
        return recorder
    }

    private fun MediaRecorder.tryRelease() {
        try {
            stop()
            release()
        } catch (exception: Exception) {
            MainLogger.Recorder.log("error: tryRelease, exception: $exception")
        }
    }

    private fun MediaRecorder.safeStart() {
        try {
            start()
        } catch (exception: Exception) {
            MainLogger.Recorder.log("error: safeStart, exception: $exception")
        }
    }

    sealed interface Event {
        data class Success(val path: String) : Event
    }
}