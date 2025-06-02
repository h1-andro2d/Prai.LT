package com.prai.te.model

internal sealed interface MainBillingState {
    data object Disconnected : MainBillingState
    data object Connected : MainBillingState
}