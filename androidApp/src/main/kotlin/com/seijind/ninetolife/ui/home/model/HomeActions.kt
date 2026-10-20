package com.seijind.ninetolife.ui.home.model

internal data class HomeActions(
    val onAddQrClick: () -> Unit,
    val onQrClick: () -> Unit,
    val onQrDismiss: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onHistoryClick: () -> Unit,
)
