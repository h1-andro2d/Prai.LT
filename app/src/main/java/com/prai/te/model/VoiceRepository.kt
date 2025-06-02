package com.prai.te.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

internal interface VoiceAccess {
    var isFriendMode: Boolean
    var friendVoiceLevel: Float
    var friendGender: FriendGender
    var friendTone: FriendTone

    var teacherVoiceLevel: Float
    var teacherTone: TeacherTone
}

internal class MockVoiceAccess : VoiceAccess {
    override var isFriendMode: Boolean = true
    override var friendVoiceLevel: Float = 1.0f
    override var friendGender: FriendGender = FriendGender.MALE
    override var friendTone: FriendTone = FriendTone.DOCTOR

    override var teacherVoiceLevel: Float = 1.0f
    override var teacherTone: TeacherTone = TeacherTone.ALLOY
}

internal class VoiceRepositoryAccess(context: Context) : VoiceAccess {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("voice_repository", Context.MODE_PRIVATE)

    override var isFriendMode: Boolean
        get() = sharedPreferences.getBoolean("is_friend_mode", false)
        set(value) = sharedPreferences.edit { putBoolean("is_friend_mode", value) }

    override var friendVoiceLevel: Float
        get() = sharedPreferences.getFloat("friend_voice_level", 1.0f)
        set(value) = sharedPreferences.edit { putFloat("friend_voice_level", value) }

    override var friendGender: FriendGender
        get() {
            val name = sharedPreferences.getString("friend_gender", FriendGender.MALE.name)
                ?: FriendGender.MALE.name
            return FriendGender.valueOf(name)
        }
        set(value) = sharedPreferences.edit { putString("friend_gender", value.name) }

    override var friendTone: FriendTone
        get() {
            val name = sharedPreferences.getString("friend_tone", FriendTone.DOCTOR.name)
                ?: FriendTone.DOCTOR.name
            return FriendTone.valueOf(name)
        }
        set(value) = sharedPreferences.edit { putString("friend_tone", value.name) }

    override var teacherVoiceLevel: Float
        get() = sharedPreferences.getFloat("teacher_voice_level", 1.0f)
        set(value) = sharedPreferences.edit { putFloat("teacher_voice_level", value) }

    override var teacherTone: TeacherTone
        get() {
            val name = sharedPreferences.getString("teacher_tone", TeacherTone.ALLOY.name)
                ?: TeacherTone.ALLOY.name
            return TeacherTone.valueOf(name)
        }
        set(value) = sharedPreferences.edit { putString("teacher_tone", value.name) }
}


internal enum class FriendGender(val description: String, val code: String) {
    MALE("남성", "Male"),
    FEMALE("여성", "Female"),
}

internal enum class FriendTone(val description: String, val code: String) {
    DOCTOR("의사", "Doctor"),
    ACTOR("배우", "Actor"),
    PROFESSOR("교수", "Professor"),
    FITNESS_COACH("운동코치", "Fitness Coach"),
    TEENAGER("청소년", "Teenager"),
    CHEERLEADER("치어리더", "Cheerleader"),
    DETECTIVE("명탐정", "Detective"),
    JAZZ_DJ("재즈 DJ", "Jazz DJ"),
    COWBOY("카우보이", "Cowboy"),
    SANTA("산타", "Santa"),
    MEDIEVAL_KNIGHT("중세 기사", "Medieval Knight"),
    ROCKSTAR("락스타", "Rockstar"),
    ROBOT("로봇", "Robot"),
    FITNESS_INSTRUCTOR("피트니스 강사", "Fitness Instructor"),
    MAD_SCIENTIST("미친 과학자", "Mad Scientist")
}

internal enum class TeacherGender(val text: String) {
    MALE("남성"),
    FEMALE("여성"),
    NEUTRAL("중성");
}

internal enum class TeacherTone(
    val gender: TeacherGender,
    val description: String,
    val code: String
) {
    ALLOY(TeacherGender.FEMALE, "신뢰감 있고\n차분한", "alloy"),
    VERSE(TeacherGender.NEUTRAL, "안정적이고\n조용한", "verse"),
    ONYX(TeacherGender.MALE, "따뜻하고\n친절한", "onyx"),
    SAGE(TeacherGender.FEMALE, "자연스럽고\n다정한", "sage"),
    ASH(TeacherGender.MALE, "밝고\n캐주얼한 느낌", "ash"),
    BALLAD(TeacherGender.NEUTRAL, "경쾌하고\n가벼운 톤", "ballad"),
    ECHO(TeacherGender.MALE, "감미롭고\n분위기 있는", "echo"),
    FABLE(TeacherGender.NEUTRAL, "차분하고\n무게감 있는", "fable"),
    NOVA(TeacherGender.FEMALE, "감성적이고\n나긋나긋함", "nova"),
    CORAL(TeacherGender.FEMALE, "자신감 있고\n또렷함", "coral"),
    SHIMMER(TeacherGender.FEMALE, "에너지 넘치고\n생동감 있음", "shimmer")
}