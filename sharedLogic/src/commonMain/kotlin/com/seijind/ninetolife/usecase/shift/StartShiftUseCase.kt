package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.ActiveShift
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

class StartShiftUseCase(
    private val settingsRepository: SettingsRepository,
    private val shiftRepository: ShiftRepository,
    private val restoreShiftAlerts: RestoreShiftAlertsUseCase,
    private val clock: Clock,
) {
    /** Returns false when a shift is already running, so it is never overwritten. */
    suspend operator fun invoke(): Boolean {
        if (shiftRepository.activeShift.first() != null) return false
        val shift = ActiveShift(
            startedAtEpochMillis = clock.now().toEpochMilliseconds(),
            plannedMinutes = settingsRepository.settings.first().shiftDurationMinutes,
        )
        shiftRepository.setActiveShift(shift = shift)
        restoreShiftAlerts()
        return true
    }
}
