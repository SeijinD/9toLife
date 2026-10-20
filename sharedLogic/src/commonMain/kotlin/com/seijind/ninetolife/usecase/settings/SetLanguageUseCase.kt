package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.settings.model.AppLanguage

class SetLanguageUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(language: AppLanguage) = settingsRepository.setLanguage(language = language)
}
