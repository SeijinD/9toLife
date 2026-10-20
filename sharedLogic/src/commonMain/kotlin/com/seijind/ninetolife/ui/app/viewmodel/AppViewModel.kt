package com.seijind.ninetolife.ui.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seijind.ninetolife.ui.app.model.AppUiState
import com.seijind.ninetolife.usecase.settings.ObserveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** App-wide appearance: theme, language, haptics. Drives the root of each platform UI. */
class AppViewModel(
    private val observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        isReady = true,
                        themeMode = settings.themeMode,
                        language = settings.language,
                        hapticsEnabled = settings.hapticsEnabled,
                        seriousMode = settings.seriousMode,
                    )
                }
            }
        }
    }
}
