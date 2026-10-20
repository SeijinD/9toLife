package com.seijind.ninetolife.ui.app.model

import com.seijind.ninetolife.domain.settings.model.AppLanguage
import com.seijind.ninetolife.domain.settings.model.ThemeMode

data class AppUiState(
    val isReady: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val hapticsEnabled: Boolean = true,
    val seriousMode: Boolean = false,
)
