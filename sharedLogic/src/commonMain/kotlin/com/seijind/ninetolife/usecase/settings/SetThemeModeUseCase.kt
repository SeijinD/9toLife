package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.settings.model.ThemeMode

class SetThemeModeUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(mode: ThemeMode) = settingsRepository.setThemeMode(mode = mode)
}
