package com.prai.te.media

import android.content.Context
import java.util.UUID

internal object MainFileManager {
    fun createAudioFilePath(context: Context): String {
        return "${context.externalCacheDir?.absolutePath}/${UUID.randomUUID()}.m4a"
    }

    fun deleteAllAudioFiles(context: Context) {
        val directory = context.externalCacheDir
        if (directory != null && directory.isDirectory) {
            directory.listFiles()?.filter {
                it.isFile && it.name.endsWith(".m4a")
            }?.forEach {
                it.delete()
            }
        }
    }
}