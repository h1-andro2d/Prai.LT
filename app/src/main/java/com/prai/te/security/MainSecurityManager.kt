package com.prai.te.security

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.prai.te.common.MainLogger

internal object MainSecurityManager {
    fun checkAppIntegrity(context: Context) {
//        val integrityManager = IntegrityManagerFactory.create(context)
//
//        val request = IntegrityTokenRequest.builder()
//            .setNonce("your-nonce") // 보안을 위해 무작위 문자열 사용
//            .build()
//
//        integrityManager.requestIntegrityToken(request)
//            .addOnSuccessListener { response ->
//                val token = response.token()
//                MainLogger.Security.log("App integrity token: $token")
//                // 서버로 토큰을 전송하여 검증
//            }
//            .addOnFailureListener { exception ->
//                MainLogger.Security.log("App integrity check fail: $exception")
//            }
    }
}