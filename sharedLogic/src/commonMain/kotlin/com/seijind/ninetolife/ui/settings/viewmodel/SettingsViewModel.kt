package com.seijind.ninetolife.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seijind.ninetolife.domain.settings.model.AppLanguage
import com.seijind.ninetolife.domain.settings.model.ThemeMode
import com.seijind.ninetolife.ui.settings.model.SettingsEvent
import com.seijind.ninetolife.ui.settings.model.SettingsUiState
import com.seijind.ninetolife.usecase.settings.AddReminderOffsetUseCase
import com.seijind.ninetolife.usecase.settings.DeleteQrPayloadUseCase
import com.seijind.ninetolife.usecase.settings.ObserveSettingsUseCase
import com.seijind.ninetolife.usecase.settings.RemoveReminderOffsetUseCase
import com.seijind.ninetolife.usecase.settings.SetBackgroundImageUseCase
import com.seijind.ninetolife.usecase.settings.SetHapticsEnabledUseCase
import com.seijind.ninetolife.usecase.settings.SetLanguageUseCase
import com.seijind.ninetolife.usecase.settings.SetSeriousModeUseCase
import com.seijind.ninetolife.usecase.settings.SetThemeModeUseCase
import com.seijind.ninetolife.usecase.settings.StepShiftDurationUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val observeSettings: ObserveSettingsUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val setLanguage: SetLanguageUseCase,
    private val stepShiftDuration: StepShiftDurationUseCase,
    private val addReminderOffset: AddReminderOffsetUseCase,
    private val removeReminderOffset: RemoveReminderOffsetUseCase,
    private val setHapticsEnabled: SetHapticsEnabledUseCase,
    private val setSeriousMode: SetSeriousModeUseCase,
    private val setBackgroundImage: SetBackgroundImageUseCase,
    private val deleteQrPayload: DeleteQrPayloadUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = Channel<SettingsEvent>(capacity = Channel.BUFFERED)
    val events: Flow<SettingsEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        themeMode = settings.themeMode,
                        language = settings.language,
                        shiftDurationMinutes = settings.shiftDurationMinutes,
                        reminderOffsetsMinutes = settings.reminderOffsetsMinutes,
                        hapticsEnabled = settings.hapticsEnabled,
                        seriousMode = settings.seriousMode,
                        hasBackgroundImage = settings.backgroundImagePath != null,
                        hasQr = settings.qrPayload != null,
                    )
                }
            }
        }
    }

    fun onThemeModeSelect(mode: ThemeMode) {
        viewModelScope.launch { setThemeMode(mode = mode) }
    }

    fun onLanguageSelect(language: AppLanguage) {
        viewModelScope.launch { setLanguage(language = language) }
    }

    fun onDurationDecrease() {
        viewModelScope.launch { stepShiftDuration(steps = -1) }
    }

    fun onDurationIncrease() {
        viewModelScope.launch { stepShiftDuration(steps = 1) }
    }

    fun onReminderAdd(offsetMinutes: Int) {
        viewModelScope.launch { addReminderOffset(offsetMinutes = offsetMinutes) }
    }

    fun onReminderRemove(offsetMinutes: Int) {
        viewModelScope.launch { removeReminderOffset(offsetMinutes = offsetMinutes) }
    }

    fun onHapticsToggle(enabled: Boolean) {
        viewModelScope.launch { setHapticsEnabled(enabled = enabled) }
    }

    fun onSeriousModeToggle(enabled: Boolean) {
        viewModelScope.launch { setSeriousMode(enabled = enabled) }
    }

    fun onBackgroundImagePicked(path: String) {
        viewModelScope.launch { setBackgroundImage(path = path) }
    }

    fun onBackgroundImageReset() {
        viewModelScope.launch { setBackgroundImage(path = null) }
    }

    fun onChangeQrClick() {
        _events.trySend(element = SettingsEvent.OpenScanner)
    }

    fun onDeleteQrConfirm() {
        viewModelScope.launch { deleteQrPayload() }
    }
}
