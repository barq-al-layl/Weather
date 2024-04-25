package com.ba.weather.model

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class AlertDialogContent(
    @StringRes val title: Int,
    @StringRes val body: Int,
)

object AlertDialogManager {
    private val dialogContent = MutableSharedFlow<AlertDialogContent>()
    val content = dialogContent.asSharedFlow()

    suspend fun showDialog(content: AlertDialogContent) {
        dialogContent.emit(content)
    }
}