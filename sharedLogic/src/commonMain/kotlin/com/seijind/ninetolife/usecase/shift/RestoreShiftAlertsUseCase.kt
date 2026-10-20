package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.shift.ShiftAlerts
import com.seijind.ninetolife.domain.shift.ShiftRepository
import kotlinx.coroutines.flow.first

/**
 * (Re)posts the alerts of the running shift with the current settings: after a reboot or app
 * update, after clocking in or undoing a clock-out, and after a setting that changes them.
 */
class RestoreShiftAlertsUseCase(
    private val settingsRepository: SettingsRepository,
    private val shiftRepository: ShiftRepository,
    private val shiftAlerts: ShiftAlerts,
) {
    suspend operator fun invoke() {
        val shift = shiftRepository.activeShift.first() ?: return
        val settings = settingsRepository.settings.first()
        shiftAlerts.schedule(
            shift = shift,
            reminderOffsetsMinutes = settings.reminderOffsetsMinutes,
            seriousMode = settings.seriousMode,
        )
    }
}
