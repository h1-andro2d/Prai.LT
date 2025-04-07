package com.prai.te.model

sealed interface MainTranslationState {
    data object None : MainTranslationState
    data class Requested(val originalText: String) : MainTranslationState
    data class Done(val originalText: String, val translatedText: String) : MainTranslationState
}