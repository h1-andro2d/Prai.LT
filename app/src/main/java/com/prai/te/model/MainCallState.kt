package com.prai.te.model

internal sealed interface MainCallState {
    data object None : MainCallState
    data object Connecting : MainCallState
    data class Active(val id: String) : MainCallState
}