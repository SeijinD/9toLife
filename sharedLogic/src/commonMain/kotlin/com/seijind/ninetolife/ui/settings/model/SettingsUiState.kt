package com.seijind.ninetolife.ui.settings.model

import com.seijind.ninetolife.domain.settings.model.AppLanguage
import com.seijind.ninetolife.domain.settings.model.AppSettings
import com.seijind.ninetolife.domain.settings.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val shiftDurationMinutes: Int = AppSettings.DefaultShiftDurationMinutes,
    val reminderOffsetsMinutes: List<Int> = AppSettings.DefaultReminderOffsetsMinutes,
    val hapticsEnabled: Boolean = true,
    val seriousMode: Boolean = false,
    val hasBackgroundImage: Boolean = false,
    val hasQr: Boolean = false,
) {
    val canDecreaseDuration: Boolean get() = shiftDurationMinutes > AppSettings.MinShiftDurationMinutes
    val canIncreaseDuration: Boolean get() = shiftDurationMinutes < AppSettings.MaxShiftDurationMinutes
    val addableReminderOffsetsMinutes: List<Int>
        get() = AppSettings.ReminderOffsetPresetsMinutes.filter { it !in reminderOffsetsMinutes && it < shiftDurationMinutes }
}

sealed interface SettingsEvent {
    data object OpenScanner : SettingsEvent
}
