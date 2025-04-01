package com.prai.te.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import com.prai.te.common.MainLogger
import kotlin.math.log10
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class MainVolumeReader(private val scope: CoroutineScope) {
    val event: SharedFlow<Event> by lazy { mutableEvent.asSharedFlow() }

    private val sampleRate = 44100
    private val bufferSize = (sampleRate * 0.2).toInt()
    private val mutableEvent = MutableSharedFlow<Event>()

    private var recorder: AudioRecord? = null
        set(value) {
            if (field != value) {
                field?.tryRelease()
                field = value
            }
        }
    private var job: Job? = null
        set(value) {
            if (field != value) {
                field?.cancel()
                field = value
            }
        }

    fun start(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        val buffer = ShortArray(bufferSize)
        try {
            recorder?.startRecording()
            job = scope.launch {
                while (isActive) {
                    val recorder = recorder ?: return@launch
                    val readSize = recorder.read(buffer, 0, buffer.size)
                    if (readSize > 0) {
                        val volume = calculateNormalizedVolume(buffer, readSize)
                        mutableEvent.emit(Event.Success(volume.toFloat()))
                    }
                }
            }
        } catch (exception: Exception) {
            MainLogger.VolumeReader.log("error: startReading, exception: $exception")
        }
    }

    fun stop() {
        recorder = null
        job = null
    }

    private fun calculateNormalizedVolume(buffer: ShortArray, readSize: Int): Double {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += buffer[i] * buffer[i]
        }
        val rms = sqrt(sum / readSize)
        val db = 20 * log10(rms.coerceAtLeast(1.0))

        val minDb = 0.0
        val maxDb = 90.31
        return ((db - minDb) / (maxDb - minDb)).coerceIn(0.0, 1.0)
    }

    private fun AudioRecord.tryRelease() {
        try {
            stop()
            release()
        } catch (exception: Exception) {
            MainLogger.VolumeReader.log("error: tryRelease, exception: $exception")
        }
    }

    sealed interface Event {
        data class Success(val level: Float) : Event
    }
}