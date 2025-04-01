package com.prai.te.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal object MainPermissionHandler {
    fun requestPermissions(activity: ComponentActivity, permission: MainPermission) {
        if (isGranted(activity, permission).not()) {
            activity.requestPermission(permission)
        }
    }

    fun isGranted(activity: ComponentActivity, permission: MainPermission): Boolean {
        return permission.items.all { isGranted(activity, it) }
    }

    private fun isGranted(activity: ComponentActivity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: MainPermission) {
        ActivityCompat.requestPermissions(
            this,
            permission.items.toTypedArray(),
            permission.code
        )
    }
}