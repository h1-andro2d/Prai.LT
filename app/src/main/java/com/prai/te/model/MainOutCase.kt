package com.prai.te.model

internal enum class MainOutCase(
    val title: String,
    val description: String,
    val buttonText: String,
    val items: List<String>
) {
    DELETE_USER(
        "회원 가입",
        "헤어지기엔 너무 아쉬워요.",
        "회원 탈퇴하기",
        listOf(
            "앱을 더 이상 사용하지 않게 됐어요.",
            "기대했던 기능이 아니에요.",
            "사용하기 어려웠어요.",
            "개인정보가 걱정됐어요.",
            "학습 방법이 제게는 맞지 않았어요.",
            "다른 서비스를 사용하고 있어요.",
            "자주 사용하지 않게 됐어요."
        )
    ),
    SUBSCRIPTION_REFUND(
        "환불하기",
        "혹시, PRAI가 놓친 게 있었을까요?",
        "구독 환불하기",
        listOf(
            "실수로 결제했어요.",
            "가격이 부담돼요.",
            "같은 구독이 두 번 결제됐어요.",
            "무료 버전으로도 충분했어요.",
        )
    ),
    SUBSCRIPTION_CANCEL(
        "구독 취소",
        "혹시, PRAI가 놓친 게 있었을까요?",
        "구독 취소하기",
        listOf(
            "기능이 생각보다 적었어요.",
            "가격이 부담돼요.",
            "자주 사용하지 않게 됐어요.",
            "무료 버전으로도 충분했어요.",
        )
    );
}