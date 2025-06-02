package com.prai.te.view.model

internal enum class BillingMessage(val title: String, val description: String) {
    BILLING_PROCESSING(
        "결제가 진행 중이에요 :)",
        "잠시만 기다려 주세요.\n앱을 종료하면 결제가 중단될 수 있어요."
    ),
    RECOVER_NO_ITEM(
        "음... 복원할 결제 내역이 없어요",
        "복원할 항목이 없습니다.\n결제한 내역이 없거나,\n다른 계정으로 결제되었을 수 있어요."
    ),
    ALREADY_PREMIUM(
        "이미 구독자입니다",
        "앱 재실행 후에, 다시 시도해 보세요!"
    ),
    BILLING_SUCCESS_BUT_PRAI_FAIL(
        "결제는 완료됐지만,\n" +
                "아직 구독이 적용되지 않았어요.",
        "서버 연결 문제로 구독 정보가 아직 반영되지 않았어요.\n 인터넷 연결을 확인하거나\n" +
                "[구매 복원하기]를 눌러주세요,"
    ),
    GOOGLE_PLAY_PURCHASE_ERROR(
        "구글 플레이에서 구매 중 에러가 발생했습니다.",
        "GooglePlay 업데이트 이후에 다시 시도해 보세요!"
    ),
    GOOGLE_PLAY_NO_ITEM(
        "앗, 일시적인 오류가 발생했어요.",
        "Google Play에서 상품 정보를 받아올 수 없습니다."
    ),
    ALREADY_HAVE_PURCHASE(
        "이미 구독중인 아이템이 있습니다.",
        "다른 계정에서 사용중일 수 있어요. 다른 계정에서 사용하고 있지 않다면 구독 복원기능을 사용해 주세요."
    ),
    PRAI_SERVER_ERROR(
        "일시적인 에러 발생",
        "PRAI서버에서 일시적인 에러가 발생했습니다."
    ),
    RECOVER_ALREADY_USED(
        "앗, 복원이 안 됐어요",
        "이 멤버십은 이미 다른 계정에서 사용 중이에요.\n사용 중인 계정으로 로그인했는지 확인해 주세요."
    ),
    RECOVER_PROCESSING(
        "조금만 기다리면 복원이 완료돼요!",
        "복원이 완료될 때까지 잠시만 기다려 주세요.\n창을 닫지 말고 조금만 기다려 주세요 :)"
    ),
    PENDING(
        "잠깐만요! 보호자의 확인이 필요해요",
        "결제 요청은 잘 전달됐어요 :)\n보호자 계정에서 승인을 하면\nPRAI와 바로 대화를 시작할 수 있어요."
    )
}