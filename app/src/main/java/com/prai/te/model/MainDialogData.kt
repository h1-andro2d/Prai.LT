package com.prai.te.model

internal enum class MainOneButtonDialogData(
    val title: String,
    val message: String,
    val buttonText: String
) {
    SUBSCRIPTION_REFUND(
        "소중한 피드백 잘 받았어요.",
        "PRAI와 많은 전화를 하지 못해 아쉬워요 😢\n실제 구독 환불은 사용하는 앱스토어를 통해 직접 요청해주세요.\n필요할 때, 언제든 다시 말 걸어 주세요 :)",
        "확인"
    ),
    SUBSCRIPTION_CANCEL(
        "소중한 피드백 잘 받았어요.",
        "PRAI와 많은 전화를 하지 못해 아쉬워요 😢\n실제 구독 취소는 사용하는 앱스토어를 통해 직접 요청해주세요.\n필요할 때, 언제든 다시 말 걸어 주세요 :)",
        "확인"
    )
}