package com.prai.te.model

internal sealed interface MainCallState {
    data object None : MainCallState
    data object Connecting : MainCallState
    data class Connected(val conversationId: String) : MainCallState
}