package com.seijind.ninetolife.ui.scanner.model

internal data class ScannerActions(
    val onClose: () -> Unit,
    val onGrantPermissionClick: () -> Unit,
    val onOpenSettingsClick: () -> Unit,
)
