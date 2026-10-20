package com.seijind.ninetolife.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

/** Shows [message] with an undo action, replacing whatever snackbar is on screen. */
internal suspend fun SnackbarHostState.showUndoSnackbar(message: String, undoLabel: String): SnackbarResult {
    currentSnackbarData?.dismiss()
    return showSnackbar(message = message, actionLabel = undoLabel, duration = SnackbarDuration.Long)
}
