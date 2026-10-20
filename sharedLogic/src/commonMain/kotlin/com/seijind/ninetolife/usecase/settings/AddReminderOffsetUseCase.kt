package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.usecase.shift.RestoreShiftAlertsUseCase

class AddReminderOffsetUseCase(
    private val settingsRepository: SettingsRepository,
    private val restoreShiftAlerts: RestoreShiftAlertsUseCase,
) {
    suspend operator fun invoke(offsetMinutes: Int) {
        settingsRepository.updateReminderOffsetsMinutes { offsets ->
            if (offsetMinutes in offsets) offsets else (offsets + offsetMinutes).sortedDescending()
        }
        restoreShiftAlerts()
    }
}
