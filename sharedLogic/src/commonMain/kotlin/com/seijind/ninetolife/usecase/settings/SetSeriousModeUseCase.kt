package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.usecase.shift.RestoreShiftAlertsUseCase

class SetSeriousModeUseCase(
    private val settingsRepository: SettingsRepository,
    private val restoreShiftAlerts: RestoreShiftAlertsUseCase,
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.setSeriousMode(enabled = enabled)
        restoreShiftAlerts()
    }
}
