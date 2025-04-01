package com.prai.te.media

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import com.prai.te.common.MainLogger
import com.prai.te.view.model.CallSegmentItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class MainPlayer(private val scope: CoroutineScope) {
    val event by lazy { mutableEvent.asSharedFlow() }

    private var player: MediaPlayer? = null
        set(value) {
            if (field != value) {
                field?.tryRelease()
                field = value
                value?.safeStart()
            }
        }
    private val mutableEvent = MutableSharedFlow<Event>()

    fun start(path: String) {
        player = createMediaPlayer(path)
    }

    fun start(segments: List<CallSegmentItem>, index: Int = 0) {
        if (segments.isEmpty() || segments.size <= index) {
            scope.launch { mutableEvent.emit(Event.End) }
            return
        }
        scope.launch { mutableEvent.emit(Event.Playing(segments[index])) }
        player = createMediaPlayer(segments[index].path) {
            if (index + 1 < segments.size) {
                start(segments, index + 1)
            } else {
                scope.launch { mutableEvent.emit(Event.End) }
            }
        }
    }

    fun stop() {
        player = null
    }

    fun getDuration(path: String): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: -1
        } catch (exception: Exception) {
            MainLogger.Player.log("error: getDuration, exception: $exception")
            -1
        } finally {
            retriever.release()
        }
    }

    private fun createMediaPlayer(path: String, onCompletion: (() -> Unit)? = null): MediaPlayer? {
        val player = MediaPlayer()

        try {
            player.apply {
                setDataSource(path)
                prepare()
                start()
                setOnCompletionListener { onCompletion?.invoke() }
            }
        } catch (exception: Exception) {
            MainLogger.Player.log("error: createMediaPlayer, exception: $exception")
            return null
        }
        return player
    }

    private fun MediaPlayer.tryRelease() {
        try {
            stop()
            release()
        } catch (exception: Exception) {
            MainLogger.Player.log("error: tryRelease, exception: $exception")
        }
    }

    private fun MediaPlayer.safeStart() {
        try {
            start()
        } catch (exception: Exception) {
            MainLogger.Player.log("error: safeStart, exception: $exception")
        }
    }

    sealed interface Event {
        data class Playing(val segment: CallSegmentItem) : Event
        data object End : Event
    }
}