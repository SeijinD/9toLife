package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.settings.model.AppSettings
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppSettings> = settingsRepository.settings
}
