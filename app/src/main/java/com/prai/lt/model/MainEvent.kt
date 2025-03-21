package com.prai.lt.model

internal sealed interface MainEvent {
    data object RecordStart: MainEvent
    data object RecordStop: MainEvent
    data class PlayStart(val path: String): MainEvent
}