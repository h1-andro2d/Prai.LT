package com.prai.te.model

internal sealed interface MainEvent {
    data object RecordStart: MainEvent
    data object RecordStop: MainEvent
    data object RecordCancel: MainEvent
    data class PlayStart(val path: String): MainEvent
    data object GoogleLoginRequest: MainEvent
    data object LogoutRequest: MainEvent
    data object RegisterUserRequest: MainEvent
    data object DeleteUserRequest: MainEvent
    data object CallStart: MainEvent
    data object CallEnd: MainEvent
    data object ConversationListOpen: MainEvent
    data class ConversationOpen(val id: String): MainEvent
    data class TranslationRequest(val text: String): MainEvent
    data object ServiceEnd: MainEvent
    data class BillingItemClick(val plan: MainPremiumPlan): MainEvent
    data object BillingRecoverClick: MainEvent
    data class SomethingOutClick(val message: String): MainEvent
}