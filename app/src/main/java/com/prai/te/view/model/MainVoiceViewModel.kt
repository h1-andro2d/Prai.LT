package com.prai.te.view.model

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.prai.te.model.FriendGender
import com.prai.te.model.VoiceRepositoryAccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class MainVoiceViewModel(
    application: Application = Application()
) : AndroidViewModel(application) {
    private val access = VoiceRepositoryAccess(application)

    val isFriendMode = MutableStateFlow(access.isFriendMode)
    val friendVoiceLevel = MutableStateFlow(access.friendVoiceLevel)
    val friendGender = MutableStateFlow(access.friendGender)
    val friendTone = MutableStateFlow(access.friendTone)
    val teacherVoiceLevel = MutableStateFlow(access.teacherVoiceLevel)
    val teacherTone = MutableStateFlow(access.teacherTone)

    var isFriendModeCache = isFriendMode.value
    var friendVoiceLevelCache = friendVoiceLevel.value
    var friendGenderCache = friendGender.value
    var friendToneCache = friendTone.value
    var teacherVoiceLevelCache = teacherVoiceLevel.value
    var teacherToneCache = teacherTone.value

    fun getModeValue(): String {
        return if (isFriendMode.value) {
            "friend"
        } else {
            "teacher"
        }
    }

    fun getVoiceValue(): String {
        if (isFriendMode.value) {
            return if (friendGender.value == FriendGender.MALE) {
                "echo"
            } else {
                "shimmer"
            }
        } else {
            return teacherTone.value.code
        }
    }

    fun getTtsOption(): String {
        return if (isFriendMode.value) {
            createTtsOption(friendTone.value.code, friendVoiceLevel.value)
        } else {
            createTtsOption(teacherTone.value.code, teacherVoiceLevel.value)
        }
    }

    private fun createTtsOption(vibe: String, speed: Float): String {
        val speedPercent = (speed * 100).toInt()
        return "Speak at ${speedPercent}% of normal speed. Use a $vibe Accent."
    }

    fun makeVoiceSettingCache() {
        isFriendModeCache = isFriendMode.value
        friendVoiceLevelCache = friendVoiceLevel.value
        friendGenderCache = friendGender.value
        friendToneCache = friendTone.value
        teacherVoiceLevelCache = teacherVoiceLevel.value
        teacherToneCache = teacherTone.value
    }

    fun saveCurrentVoiceSetting() {
        access.isFriendMode = isFriendMode.value
        access.friendVoiceLevel = friendVoiceLevel.value
        access.friendGender = friendGender.value
        access.friendTone = friendTone.value
        access.teacherVoiceLevel = teacherVoiceLevel.value
        access.teacherTone = teacherTone.value

        Firebase.analytics.logEvent(
            "voice_settings_saved2",
            bundleOf(
                "isFriendMode" to isFriendMode.value,
                "friendVoiceLevel" to friendVoiceLevel.value,
                "friendGender" to friendGender.value.name,
                "friendTone" to friendTone.value.name,
                "teacherVoiceLevel" to teacherVoiceLevel.value,
                "teacherTone" to teacherTone.value.name
            )
        )
        makeVoiceSettingCache()
    }

    fun rollbackVoiceSetting() {
        viewModelScope.launch {
            delay(300L)
            isFriendMode.value = isFriendModeCache
            friendVoiceLevel.value = friendVoiceLevelCache
            friendGender.value = friendGenderCache
            friendTone.value = friendToneCache
            teacherVoiceLevel.value = teacherVoiceLevelCache
            teacherTone.value = teacherToneCache
        }
    }
}