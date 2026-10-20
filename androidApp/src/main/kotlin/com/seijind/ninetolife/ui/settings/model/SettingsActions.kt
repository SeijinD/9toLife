package com.seijind.ninetolife.ui.settings.model

import com.seijind.ninetolife.domain.settings.model.AppLanguage
import com.seijind.ninetolife.domain.settings.model.ThemeMode

internal data class SettingsActions(
    val onThemeModeSelect: (mode: ThemeMode) -> Unit,
    val onLanguageSelect: (language: AppLanguage) -> Unit,
    val onDurationDecrease: () -> Unit,
    val onDurationIncrease: () -> Unit,
    val onReminderAdd: (offsetMinutes: Int) -> Unit,
    val onReminderRemove: (offsetMinutes: Int) -> Unit,
    val onAllowExactRemindersClick: () -> Unit,
    val onHapticsToggle: (enabled: Boolean) -> Unit,
    val onSeriousModeToggle: (enabled: Boolean) -> Unit,
    val onPickBackgroundClick: () -> Unit,
    val onResetBackgroundClick: () -> Unit,
    val onChangeQrClick: () -> Unit,
    val onDeleteQrConfirm: () -> Unit,
)
