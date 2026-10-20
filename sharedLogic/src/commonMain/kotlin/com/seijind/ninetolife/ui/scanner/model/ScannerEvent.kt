package com.seijind.ninetolife.ui.scanner.model

sealed interface ScannerEvent {
    data object QrSaved : ScannerEvent
}
