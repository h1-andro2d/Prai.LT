package com.prai.lt.media

import android.media.MediaPlayer
import com.prai.lt.common.MainLogger

internal object MainPlayer {
    private var player: MediaPlayer? = null
        set(value) {
            if (field != value) {
                field?.tryRelease()
                field = value
                value?.safeStart()
            }
        }

    fun start(path: String) {
        player = createMediaPlayer(path)
    }

    fun stop() {
        player = null
    }

    private fun createMediaPlayer(path: String): MediaPlayer? {
        val player = MediaPlayer()

        try {
            player.apply {
                setDataSource(path)
                prepare()
                start()
            }
        } catch (e: Exception) {
            MainLogger.Player.log("error: createMediaPlayer, exception: $e")
            return null
        }
        return player
    }

    private fun MediaPlayer.tryRelease() {
        stop()
        release()
    }

    private fun MediaPlayer.safeStart() {
        try {
            start()
        } catch (exception: Exception) {
            MainLogger.Player.log("error: safeStart, exception: $exception")
        }
    }
}