package com.prai.te.model

internal enum class Gender(val text: String) {
    MALE("남성"),
    FEMALE("여성");
}

internal enum class MainVoiceSettingItem(
    val gender: Gender,
    val description: String,
    val code: String
) {
    FEMALE_1(Gender.FEMALE, "신뢰감 있고\n차분한", "alloy"),
    FEMALE_2(Gender.FEMALE, "안정적이고\n조용한", "verse"),
    FEMALE_3(Gender.FEMALE, "자연스럽고\n다정한", "sage"),
    FEMALE_4(Gender.FEMALE, "감성적이고\n나긋나긋한", "nova"),
    FEMALE_5(Gender.FEMALE, "자신감 있고\n또렷한", "coral"),
    FEMALE_6(Gender.FEMALE, "에너지 넘치고\n생동감 있는", "shimmer"),
    MALE_1(Gender.MALE, "따뜻하고\n친절한", "onyx"),
    MALE_2(Gender.MALE, "밝고\n캐주얼한", "ash"),
    MALE_3(Gender.MALE, "경쾌하고\n가벼운", "ballad"),
    MALE_4(Gender.MALE, "감미로운 음색\n분위기 있는", "echo"),
    MALE_5(Gender.MALE, "공감력 있는\n이야기 톤", "fable")
}

internal enum class MainVibeSettingItem(val description: String, val code: String) {
    FRIENDLY("친근한", "Friendly"),
    CALM("차분한", "Calm"),
    SINCERE("진실된", "Sincere"),
    FITNESS_COACH("운동코치", "Fitness Coach"),
    MAD_SCIENTIST("미친 과학자", "Mad Scientist"),
    CHEERLEADER("치어리더", "Cheerleader"),
    DETECTIVE("탐정", "Detective"),
    JAZZ_DJ("재즈 DJ", "Jazz DJ"),
    COWBOY("카우보이", "Cowboy"),
    SANTA("산타", "Santa"),
    MIDDLE_AGE_KNIGHT("중세 기사", "Medieval Knight"),
    CHEF("요리사", "Chef")
}