package com.seijind.ninetolife.ui.scanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seijind.ninetolife.ui.scanner.model.ScannerEvent
import com.seijind.ninetolife.usecase.settings.SaveQrPayloadUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val saveQrPayload: SaveQrPayloadUseCase,
) : ViewModel() {

    private val _events = Channel<ScannerEvent>(capacity = Channel.BUFFERED)
    val events: Flow<ScannerEvent> = _events.receiveAsFlow()

    private var isSaved = false

    /** The camera reports the same code many times per second; only the first one counts. */
    fun onQrScanned(payload: String) {
        if (isSaved || payload.isBlank()) return
        isSaved = true
        viewModelScope.launch {
            saveQrPayload(payload = payload)
            _events.trySend(element = ScannerEvent.QrSaved)
        }
    }
}
