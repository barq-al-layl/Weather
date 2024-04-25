package com.ba.weather.model

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarManager {
    private val messageFlow = MutableSharedFlow<Int>()
    val message = messageFlow.asSharedFlow()

    suspend fun showMessage(@StringRes snackbarMessage: Int) {
        messageFlow.emit(snackbarMessage)
    }
}