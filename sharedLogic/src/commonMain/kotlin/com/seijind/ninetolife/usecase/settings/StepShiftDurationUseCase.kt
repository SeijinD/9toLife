package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.settings.model.AppSettings

class StepShiftDurationUseCase(
    private val settingsRepository: SettingsRepository,
) {
    /** Moves the shift duration by [steps] of [AppSettings.ShiftDurationStepMinutes], kept inside the allowed range. */
    suspend operator fun invoke(steps: Int) {
        settingsRepository.updateShiftDurationMinutes { current ->
            (current + steps * AppSettings.ShiftDurationStepMinutes).coerceIn(
                minimumValue = AppSettings.MinShiftDurationMinutes,
                maximumValue = AppSettings.MaxShiftDurationMinutes,
            )
        }
    }
}
