package com.prai.te.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

internal object MainNavigator {
    fun startAskWebView(context: Context) {
        startWebView(
            context,
            "https://humdrum-rosehip-5a9.notion.site/1af2f86a4d0180e0af33e9fc1fdbd475"
        )
    }

    fun startRuleWebView(context: Context) {
        startWebView(
            context,
            "https://humdrum-rosehip-5a9.notion.site/1af2f86a4d01800f8961cb4ce9e9cc0f"
        )
    }

    fun startPrivacyWebView(context: Context) {
        startWebView(
            context,
            "https://humdrum-rosehip-5a9.notion.site/1af2f86a4d0180028c83e03d26aeceb6"
        )
    }

    fun openAppStore(context: Context) {
        val appPackageName = context.packageName

        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=$appPackageName".toUri()
                )
            )
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                )
            )
        }
    }

    private fun startWebView(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}