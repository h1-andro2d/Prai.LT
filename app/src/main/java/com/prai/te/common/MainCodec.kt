package com.prai.te.common

import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

internal object MainCodec {
    fun encodeFilePathToBase64(path: String): String {
        val bytes = FileInputStream(File(path)).use { inputStream ->
            inputStream.readBytes()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun decodeBase64ToFile(encoded: String, path: String): Boolean {
        try {
            val decodedBytes = Base64.decode(encoded, Base64.DEFAULT)
            FileOutputStream(File(path)).use { outputStream ->
                outputStream.write(decodedBytes)
            }
        } catch (exception: Exception) {
            MainLogger.Codec.log(exception, "error: decodeBase64ToFile, exception: $exception")
            return false
        }
        return true
    }
}